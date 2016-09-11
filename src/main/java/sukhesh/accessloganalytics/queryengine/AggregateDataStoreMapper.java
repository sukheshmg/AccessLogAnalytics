package sukhesh.accessloganalytics.queryengine;

import sukhesh.accessloganalytics.querymodel.Function;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;
import sukhesh.accessloganalytics.util.BeanLookupHelper;

import java.util.List;

/**
 * get an aggregated data store for the given dimension
 * Created by sukhesh on 09/09/16.
 */
public class AggregateDataStoreMapper {
    public static AggregatedDataStore getAggregateDataStore(String[] dimension, Function[] functions) {
        List<AggregatedDataStore> dataStoreList = BeanLookupHelper.INSTANCE.getAllAggregatedDataStores();
        for(AggregatedDataStore dataStore:dataStoreList) {
            if(dataStore.getDimensions().contains(dimension[0])) {
                return dataStore;
            }
        }
        return null;
    }
}
