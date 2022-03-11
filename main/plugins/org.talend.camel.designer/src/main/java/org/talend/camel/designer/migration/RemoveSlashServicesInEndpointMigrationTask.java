// ============================================================================
package org.talend.camel.designer.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.migration.AbstractItemMigrationTask;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;

public class RemoveSlashServicesInEndpointMigrationTask extends AbstractItemMigrationTask {

    private String[] nodesName = {"cSOAP","cREST","tRESTRequest"};
    private Map<String, String> nodeNameEndpointNameMap = new HashMap<String, String>(){
        private static final long serialVersionUID = 1L;
        {
            put("cSOAP", "ADDRESS");
            put("cREST", "URL");
            put("tRESTRequest", "REST_ENDPOINT");
        }
    };
    
    @Override
    public List<ERepositoryObjectType> getTypes() {
        List<ERepositoryObjectType> toReturn = new ArrayList<ERepositoryObjectType>();
        toReturn.add(ERepositoryObjectType.PROCESS);
        toReturn.add(ERepositoryObjectType.JOBLET);
        toReturn.add(ERepositoryObjectType.TEST_CONTAINER);
        toReturn.add(ERepositoryObjectType.PROCESS_ROUTE);
        toReturn.add(ERepositoryObjectType.PROCESS_ROUTE_DESIGN);
        toReturn.add(ERepositoryObjectType.PROCESS_ROUTE_MICROSERVICE);
        toReturn.add(ERepositoryObjectType.PROCESS_ROUTELET);
        return toReturn;
    }
    
    @Override
    public ExecutionResult execute(Item item) {
        ProcessType processType = null;
        if (item instanceof ProcessItem) {
            processType = ((ProcessItem) item).getProcess();
        }

        boolean modified = false;

        for (Object o : processType.getNode()) {
            if (o instanceof NodeType) {
                NodeType currentNode = (NodeType) o;
                String componentName = currentNode.getComponentName();
                if (Arrays.asList(nodesName).contains(componentName)) {
                    try {
                        removeSlashServicesInEndpoint(currentNode);
                        modified = true;
                    } catch (PersistenceException e) {
                        ExceptionHandler.process(e);
                        return ExecutionResult.FAILURE;
                    }
                }
            }
        }
        if (modified) {
            return ExecutionResult.SUCCESS_NO_ALERT;
        } else {
            return ExecutionResult.NOTHING_TO_DO;
        }
    }
    
	@Override
	public Date getOrder() {
		return new GregorianCalendar(2022, 03, 03, 00, 00, 00).getTime();
	}

	private boolean needKeepEndpoint(String endpoint) {
	    return endpoint.startsWith("\"http://") || endpoint.startsWith("\"https://") || endpoint.startsWith("context.");
	}

    private void removeSlashServicesInEndpoint(NodeType currentNode) throws PersistenceException {
        String nodeName = currentNode.getComponentName();
        String endpointEleName = nodeNameEndpointNameMap.get(nodeName);

        for (Object e : currentNode.getElementParameter()) {
            ElementParameterType p = (ElementParameterType) e;
            if (p.getName().equals(endpointEleName)) {

                String endpoint = p.getValue().replace(" ", "");
                // will keep absolute URL as it is. Only remove relative URL's first
                // "/services".
                if (!needKeepEndpoint(endpoint)) {
                    endpoint = endpoint.substring("/sevices".length());
                }
            }
        }
    }
}
