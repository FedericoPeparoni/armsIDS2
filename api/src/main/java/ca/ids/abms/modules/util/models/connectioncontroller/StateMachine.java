package ca.ids.abms.modules.util.models.connectioncontroller;

import org.apache.log4j.Logger;

public class StateMachine {

    private Logger LOG = Logger.getLogger(StateMachine.class);
    // Time is in milliseconds
    public int DEFUALUT_WARNING_INTEVAL=10000;

    private TimerService service;


    private ServiceState state = ServiceState.CONNECTION_MISSING;

    public boolean do_action(ServiceOperations p_operation, boolean p_test) {
        boolean p_done = false;
        switch (p_operation) {
            case CONNECT:
                p_done = connect(p_test);
                break;
            case DISCONNECT:
                p_done = disconnect(p_test);
                break;
            case CONNECTION_WARNING:
                p_done = connect_warning(p_test);
                break;
            case CONNECTION_FAIL:
                p_done = connect_fail(p_test);
                break;
            case CONNECT_OK:
                p_done = connect_ok(p_test);
                break;
        }

        return p_done;
    }

    private boolean connect_warning(boolean p_test) {
        boolean p_done = false;
        if (getState().equals(ServiceState.RUNNING)) {
            service = new TimerService(this, new Integer(DEFUALUT_WARNING_INTEVAL));
            performOperation(ServiceState.WARNING, p_test);
            p_done = true;
        }
        return p_done;
    }

    private boolean connect_ok(boolean p_test) {
        boolean p_done = false;
        service = null;
        if (getState().equals(ServiceState.RUNNING)) {
            p_done = true;
        }
        if ((getState().equals(ServiceState.WARNING)) || (getState().equals(ServiceState.FAIL))) {
            performOperation(ServiceState.RUNNING, p_test);
            p_done = true;
        }
        return p_done;
    }

    private boolean connect_fail(boolean p_test) {
        boolean p_done = false;
        service = null;
        if (getState().equals(ServiceState.FAIL)) {
            p_done = true;
        }
        if ((getState().equals(ServiceState.WARNING)) || (getState().equals(ServiceState.RUNNING))) {
            performOperation(ServiceState.FAIL, p_test);
            p_done = true;
        }
        return p_done;
    }

    private boolean disconnect(boolean p_test) {
        boolean p_done = true;
        service = null;
        performOperation(ServiceState.CONNECTION_MISSING, p_test);
        return p_done;
    }

    private boolean connect(boolean p_test) {
        boolean p_done = false;
        service = null;
        if (getState().equals(ServiceState.CONNECTION_MISSING)) {
            performOperation(ServiceState.RUNNING, p_test);
            p_done = true;
        }
        return p_done;
    }

    private void performOperation(ServiceState p_nextState, boolean p_test) {
        if (false == p_test) {
            state = p_nextState;
        }
    }

    public boolean do_action(ServiceOperations p_operation) {
        return do_action(p_operation, false);
    }

    public ServiceState getState() {
        return state;
    }


    public void printConnectionStatus() {
        LOG.trace("Connection status: " + getState().toString());
        if (getState().equals(ServiceState.WARNING)) {
            LOG.warn("Connection status: " + getState().toString());
        }
        if (getState().equals(ServiceState.FAIL)) {
            LOG.error("Connection status: " + getState().toString());
        }
    }
}
