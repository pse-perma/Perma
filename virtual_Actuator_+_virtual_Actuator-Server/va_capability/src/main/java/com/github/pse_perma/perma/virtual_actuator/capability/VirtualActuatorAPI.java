package com.github.pse_perma.perma.virtual_actuator.capability;

import de.fraunhofer.iosb.ilt.sta.NotFoundException;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Entity;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class VirtualActuatorAPI {

    private VirtualActuatorConfig config;
    private SensorThingsService stService;
    private static Logger logger = LoggerFactory.getLogger(VirtualActuatorAPI.class);


    public VirtualActuatorAPI(VirtualActuatorConfig config, SensorThingsService stService) {
        this.config = config;
        this.stService = stService;
    }


    public SensorThingsService getStService() {
        return this.stService;
    }


    /**
     * Creates or updates the provided entity on the Sensorthings-server, and sets the entities id to match the server.
     * When creating an entity, the server-assigned id is saved to idMap using the provided <code>entityName</code> an the entity type
     * for the key. The id is set on <code>entity</code> as well. At subsequent calls with the same entity type and <code>entityName</code>, the id The corresponding Server-entities are reused even after applica
     *
     * @param entity The entity to create or update.
     * @param entityName VA-internal name for this entity to distinguish from other entities (of the same type).
     *                   Used for idMap. Can be <code>null</code>, in this case the entity is not managed using the idMap.
     * @return The entity as retrieved from the server. Id and service are set.
     * @throws ServiceFailureException If server-communication failed.
     */
    public <T extends Entity<T>> T createOrUpdateEntity(T entity, String entityName) throws ServiceFailureException {
        if (entityName != null) {
            entity.setId(this.config.getEntityId(entity, entityName));
        }

        T serverEntity = null;
        if (entity.getId() != null) {
            try {
                serverEntity = entity.getDao(this.stService).find(entity.getId());
            } catch (NotFoundException ignored) { }  // entity was likely deleted on server
        }

        if (serverEntity == null) {  // create and register a new entity
            this.stService.create(entity);
            serverEntity = entity;
            if (entityName != null) {
                this.logger.debug("Created a new entry for {} {} in server.", entity.getType().getName(), entityName);
                this.config.setEntityId(serverEntity, entityName);
            }
        } else {  // found existing entry in server, update
            if (entityName != null) {
                this.logger.debug("Found existing entry for {} {} in server.", entity.getType().getName(), entityName);
            }

            if (!serverEntity.equals(entity)) {
                this.stService.update(entity);
                this.logger.debug("Updated existing entry for {}{} ({}) in server.", entity.getType().getName(),
                        entityName == null ? "" : " " + entityName, entity.getId());
            }
        }

        serverEntity.setService(this.stService);
        return serverEntity;
    }
}
