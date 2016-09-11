package sukhesh.accessloganalytics.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * loading config from command line arguments
 * Created by sukhesh on 08/09/16.
 */
public enum ConfigLoader {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    public void loadConfig() {
        logger.info("Reading configuration from environment");

        String _servicePort = System.getProperty("service.port");
        if(StringUtils.isNotEmpty(_servicePort)) {
            logger.info("Setting service port to {} from environment configuration", new Object[]{_servicePort});
            try {
                GlobalConfig.INSTANCE.setServicePort(Integer.parseInt(_servicePort));
            } catch (NumberFormatException e) {
                logger.error("Exception parsing " + _servicePort, e);
            }
        } else {
            logger.info("Using default port " + GlobalConfig.INSTANCE.getServicePort());
        }

        String logUri = System.getProperty("log.uri");
        if(StringUtils.isNotEmpty(logUri)) {
            logger.info("Setting uri to " + logUri);
            GlobalConfig.INSTANCE.setLogUri(logUri);
        } else {
            logger.info("Usign default log uri " + GlobalConfig.INSTANCE.getLogUri());
        }

        String maxRecordsInMemory = System.getProperty("max.records.inmemory");
        if(StringUtils.isNotEmpty(maxRecordsInMemory)) {
            logger.info("Setting max.records.inmemory to " + maxRecordsInMemory);
            try {
                GlobalConfig.INSTANCE.setMaxRecordsInMemory(Integer.parseInt(maxRecordsInMemory));
            } catch (NumberFormatException e) {
                logger.error("Invalid configuration max.records.inmemory" );
            }
        } else {
            logger.info("Using default max.records.inmemory " + GlobalConfig.INSTANCE.getMaxRecordsInMemory());
        }

        String _sleeptime = System.getProperty("read.sleep.time");
        if(StringUtils.isNotEmpty(_sleeptime)) {
            logger.info("Setting read.sleep.time to " + _sleeptime);
            try {
                GlobalConfig.INSTANCE.setSleepBetweenReads(Integer.parseInt(_sleeptime));
            } catch (NumberFormatException e) {
                logger.error("Invalid configuration read.sleep.time");
            }
        }
    }
}
