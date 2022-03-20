package pl.szczurowsky.ratorm.serializers;

public interface Serializer {

    /**
     * Serialize provided object to string
     * @param providedObject Object
     * @return string to store in database
     */
    String serialize(Object providedObject);
    
}
