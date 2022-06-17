package pl.szczurowsky.ratorm.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import pl.szczurowsky.ratorm.Database;
import pl.szczurowsky.ratorm.annotations.DBModel;
import pl.szczurowsky.ratorm.annotations.Field;
import pl.szczurowsky.ratorm.credentials.Credentials;
import pl.szczurowsky.ratorm.exceptions.AlreadyConnectedException;
import pl.szczurowsky.ratorm.exceptions.ModelException;
import pl.szczurowsky.ratorm.exceptions.NotConnectedToDatabaseException;
import pl.szczurowsky.ratorm.exceptions.OperationException;
import pl.szczurowsky.ratorm.model.Model;
import pl.szczurowsky.ratorm.util.SerializerUtil;

import java.lang.reflect.Type;
import java.util.*;

public class MongoDB extends Database {

    private MongoClient client;

    private MongoDatabase database;

    @Override
    public boolean isConnectionValid() {
        try {
            this.client.listDatabases();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void terminateConnection() throws NotConnectedToDatabaseException {
        if (connected) {
            this.client.close();
            this.connected = false;
        }
        else
            throw new NotConnectedToDatabaseException();
    }

    @Override
    public void connect(String URI) throws AlreadyConnectedException {
        if (connected)
            throw new AlreadyConnectedException();
        MongoClientURI mongoClientURI = new MongoClientURI(URI);
        this.client = MongoClients.create(URI);
        this.database = this.client.getDatabase(Objects.requireNonNull(mongoClientURI.getDatabase()));
        this.connected = true;
    }

    @Override
    public void connect(Credentials credentials) throws AlreadyConnectedException {
        if (connected)
            throw new AlreadyConnectedException();
        ServerAddress serverAddress = new ServerAddress(credentials.getHost(), credentials.getPort());
        MongoCredential credential = MongoCredential.createCredential(credentials.getUsername(), credentials.getDatabaseName(), credentials.getPassword().toCharArray());
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(
                        serverAddress
                )))
                .credential(credential)
                .build();
        this.client = MongoClients.create(settings);
        this.database = this.client.getDatabase(credentials.getDatabaseName());
        this.connected = true;
    }

    @Override
    public void initModel(Collection<Class<? extends Model>> models) throws ModelException {
        for (Class<? extends Model> model : models) {
            if (!model.isAnnotationPresent(DBModel.class))
                throw new ModelException("Model " + model.getName() + " is not annotated with @ModelAnnotation");
            int primaryKeys = 0;
            for (java.lang.reflect.Field declaredField : model.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Field.class))
                    if (declaredField.getAnnotation(Field.class).isPrimaryKey())
                        primaryKeys++;
            }
            if (primaryKeys > 1)
                throw new ModelException("Model " + model.getName() + " has more than one primary key");
            String tableName = model.getAnnotation(DBModel.class).tableName();
            if (this.models.containsKey(tableName))
                throw new ModelException("Model " + model.getName() + " has the same table name as model " + this.models.get(tableName).getName());
            if (!this.database.listCollectionNames().into(new ArrayList<>()).contains(tableName))
                this.database.createCollection(tableName);
            this.models.put(tableName, model);
        }
    }

    @Override
    public <T extends Model> List<T> fetchAll(Class<T> model) throws OperationException {
        if (!connected)
            throw new OperationException("Database is not connected");
        if (!this.models.containsValue(model))
            throw new OperationException("Model " + model.getName() + " is not initialized");
        MongoCollection<Document> collection = this.database.getCollection(model.getAnnotation(DBModel.class).tableName());
        return deserialize(model, collection.find());
    }

    @Override
    public <T extends Model> List<T> fetchMatching(Class<T> modelClass, String key, Object value) throws OperationException {
        try {
            if (!connected)
                throw new OperationException("Database is not connected");
            if (!this.models.containsValue(modelClass))
                throw new OperationException("Model " + modelClass.getName() + " is not initialized");
            MongoCollection<Document> collection = this.database.getCollection(modelClass.getAnnotation(DBModel.class).tableName());
            return deserialize(modelClass, collection.find(new Document(key, SerializerUtil.serializeValue(value.getClass(), value, serializers, complexSerializers))));
        } catch (Exception e) {
            throw new OperationException("Error while fetching matching documents \nException:\n" + e.getMessage());
        }
    }

    @Override
    public <T extends Model> void save(T model) throws OperationException {
        this.saveToDatabase(model, new HashMap<>());
    }

    @Override
    public <T extends Model> void save(T model, HashMap<String, Object> options) throws OperationException {
        this.saveToDatabase(model, options);
    }

    @Override
    public <T extends Model> void saveMany(Collection<T> model) throws OperationException {
        this.saveManyToDatabase(model, new HashMap<>());
    }

    @Override
    public <T extends Model> void saveMany(Collection<T> model, HashMap<String, Object> options) throws OperationException {
        this.saveManyToDatabase(model, options);
    }

    @Override
    public <T extends Model> void delete(T model) throws OperationException {
        this.deleteFromDatabase(model, new HashMap<>());
    }

    @Override
    public <T extends Model> void delete(T model, HashMap<String, Object> options) throws OperationException {
        this.delete(model, options);
    }

    @Override
    public <T extends Model> void deleteMany(Collection<T> models) throws OperationException {
        this.deleteManyFromDatabase(models, new HashMap<>());
    }

    @Override
    public <T extends Model> void deleteMany(Collection<T> models, HashMap<String, Object> options) throws OperationException {
        this.deleteManyFromDatabase(models, options);
    }

    /**
     * Starts session
     * @return Session
     */
    public ClientSession startSession() {
        return this.client.startSession();
    }

    private <T extends Model> void deleteManyFromDatabase(Collection<T> models, HashMap<String, Object> options) throws OperationException {
        try {
            if (!connected)
                throw new OperationException("Database is not connected");
            if (models.isEmpty())
                return;
            Class<T> modelClass = (Class<T>) models.iterator().next().getClass();
            if (!this.models.containsValue(modelClass))
                throw new OperationException("Model " + modelClass.getName() + " is not initialized");
            if (!modelClass.isAnnotationPresent(DBModel.class))
                throw new OperationException("Model " + modelClass.getName() + " is not annotated with @ModelAnnotation");
            List<WriteModel<Document>> writes = new ArrayList<>();
            for (T model : models) {
                Document serializedModel = serialize(modelClass, model);
                Document document =  serializedModel.get("value", Document.class);
                writes.add(
                        new DeleteOneModel<>(
                                document
                        )
                );
                model.unlockWrite();
            }
            if (options.containsKey("MongoDB.session"))
                this.database.getCollection(modelClass.getAnnotation(DBModel.class).tableName()).bulkWrite((ClientSession) options.get("MongoDB.session"), writes);
            else
                this.database.getCollection(modelClass.getAnnotation(DBModel.class).tableName()).bulkWrite(writes);
            for (T model : models) {
                model.unlockWrite();
            }
        } catch (Exception e) {
            throw new OperationException("Error while saving models " + "\nException:\n" + e);
        }
    }

    private <T extends Model> void deleteFromDatabase(T model, HashMap<String, Object> options) throws OperationException {
        try {
            if (!connected)
                throw new OperationException("Database is not connected");
            if (models.isEmpty())
                return;
            Class<T> modelClass = (Class<T>) model.getClass();
            if (!this.models.containsValue(modelClass))
                throw new OperationException("Model " + modelClass.getName() + " is not initialized");
            if (!modelClass.isAnnotationPresent(DBModel.class))
                throw new OperationException("Model " + modelClass.getName() + " is not annotated with @ModelAnnotation");
            model.lockWrite();
            this.database.getCollection(modelClass.getAnnotation(DBModel.class).tableName()).deleteOne(serialize(modelClass, model).get("value", Document.class));
            model.unlockWrite();
        } catch (Exception e) {
            throw new OperationException("Error while deleting model " + model.getClass().getName());
        }
    }

    private <T extends Model> void saveManyToDatabase(Collection<T> models, HashMap<String, Object> options) throws OperationException {
        try {
            if (!connected)
                throw new OperationException("Database is not connected");
            if (models.isEmpty())
                return;
            Class<T> modelClass = (Class<T>) models.iterator().next().getClass();
            if (!this.models.containsValue(modelClass))
                throw new OperationException("Model " + modelClass.getName() + " is not initialized");
            if (!modelClass.isAnnotationPresent(DBModel.class))
                throw new OperationException("Model " + modelClass.getName() + " is not annotated with @ModelAnnotation");
            List<WriteModel<Document>> writes = new ArrayList<>();
            for (T model : models) {
                Document serializedModel = serialize(modelClass, model);
                Document document =  serializedModel.get("value", Document.class);
                Document key =  serializedModel.get("key", Document.class);
                if (key.size() == 0)
                    writes.add(
                            new InsertOneModel<>(
                                    document
                            )
                    );
                else
                    writes.add(
                            new UpdateOneModel<>(
                                    key,
                                    new Document("$set", document),
                                    new UpdateOptions().upsert(true)
                            )
                    );
                model.unlockWrite();
            }
            if (options.containsKey("MongoDB.session"))
                this.database.getCollection(modelClass.getAnnotation(DBModel.class).tableName()).bulkWrite((ClientSession) options.get("MongoDB.session"), writes);
            else
                this.database.getCollection(modelClass.getAnnotation(DBModel.class).tableName()).bulkWrite(writes);
            for (T model : models) {
                model.unlockWrite();
            }
        } catch (Exception e) {
            throw new OperationException("Error while saving models " + "\nException:\n" + e);
        }
    }

    private <T extends Model> void saveToDatabase(T model, HashMap<String, Object> options) throws OperationException {
        try {
            if (!connected)
                throw new OperationException("Database is not connected");
            Class<T> modelClass = (Class<T>) model.getClass();
            if (!this.models.containsValue(modelClass))
                throw new OperationException("Model " + modelClass.getName() + " is not initialized");
            if (!modelClass.isAnnotationPresent(DBModel.class))
                throw new OperationException("Model " + modelClass.getName() + " is not annotated with @ModelAnnotation");
            Document serializedModel = serialize(modelClass, model);
            Document document =  serializedModel.get("value", Document.class);
            Document key =  serializedModel.get("key", Document.class);
            if (options.containsKey("MongoDB.session"))
                if (key.size() == 0)
                    database.getCollection(modelClass.getAnnotation(DBModel.class).tableName()).insertOne((ClientSession) options.get("MongoDB.session"), document);
                else
                    database.getCollection(modelClass.getAnnotation(DBModel.class).tableName()).updateOne((ClientSession) options.get("MongoDB.session"), key, new Document("$set", document), new UpdateOptions().upsert(true));
            else
                if (key.size() == 0)
                    database.getCollection(modelClass.getAnnotation(DBModel.class).tableName()).insertOne(document);
                else
                    database.getCollection(modelClass.getAnnotation(DBModel.class).tableName()).updateOne(key, new Document("$set", document), new UpdateOptions().upsert(true));
        } catch (Exception e) {
            throw new OperationException("Error while saving model " + model.getClass().getName() + "\nException:\n" + e);
        }
    }



    private <T extends Model> List<T> deserialize(Class<T> classType, FindIterable<Document> fetchedDocuments) throws OperationException {
        try {
            List<T> deserialized = new ArrayList<>();
            for (Document fetchedDocument : fetchedDocuments) {
                boolean toBeFixed = false;
                T model = classType.newInstance();
                for (java.lang.reflect.Field declaredField : classType.getDeclaredFields()) {
                    if (declaredField.isAnnotationPresent(Field.class)) {
                        declaredField.setAccessible(true);
                        String name = declaredField.getAnnotation(Field.class).name();
                        if (name.equals("")) {
                            name = declaredField.getName();
                        }
                        Object value = fetchedDocument.get(name);
                        if (value == null) {
                            value = declaredField.get(classType);
                            toBeFixed = true;
                            declaredField.set(model, value);
                        }
                        else
                            declaredField.set(model, SerializerUtil.deserializeValue(declaredField.getType(), (String) value, serializers, complexSerializers));
                        deserialized.add(model);
                    }
                    if (toBeFixed)
                        this.save(model);
                }
            }
            return deserialized;
        } catch (Exception e) {
            throw new OperationException("Error while deserializing model " + classType.getName() + "\nException: " + e.getMessage());
        }
    }

    private <T extends Model> Document serialize(Class<T> classType, T model) throws OperationException {
        try {
            Document document = new Document();
            Document key = new Document();
            for (java.lang.reflect.Field declaredField : classType.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Field.class)) {
                    declaredField.setAccessible(true);
                    String name = declaredField.getAnnotation(Field.class).name();
                    if (name.equals("")) {
                        name = declaredField.getName();
                    }
                    Type fieldType = declaredField.getGenericType();
                    Object value = declaredField.get(model);
                    String serialized = SerializerUtil.serializeValue(declaredField.getType(), value, serializers, complexSerializers);
                    document.put(name, serialized);
                    if (declaredField.getAnnotation(Field.class).isPrimaryKey()) {
                        key.put(name, serialized);
                    }
                }
            }
            return new Document("key", key).append("value", document);
        } catch (Exception e) {
            throw new OperationException("Error while deserializing model " + classType.getName() + "\nException:\n " + e.getMessage());
        }
    }


}
