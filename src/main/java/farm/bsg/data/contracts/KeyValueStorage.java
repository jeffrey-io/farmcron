package farm.bsg.data.contracts;

/**
 * this defines a minimal storage layer that assumes you have a global keyspace that you can write and read values from. Furthermore, it assumes that you can scan items by prefix. This is model is designed to work with a storage tier like S3, disk, or an in-memory index.
 */
public interface KeyValueStorage extends KeyValueStoragePut, KeyValueStorageRead {

}
