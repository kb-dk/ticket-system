package dk.statsbiblioteket.doms.authchecker;

import dk.statsbiblioteket.doms.domsutil.surveyable.Surveyable;
import dk.statsbiblioteket.doms.domsutil.surveyable.Status;
import dk.statsbiblioteket.doms.domsutil.surveyable.StatusMessage;
import dk.statsbiblioteket.doms.domsutil.surveyable.Severity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 23, 2010
 * Time: 5:11:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthCheckerSurveyable implements Surveyable {
    /** The name this status reports. */
    private static final String SURVEYABLE_NAME = "DomsAuthchecker";

    /** Log for this class. */
    private final Log log = LogFactory.getLog(getClass());

    /**
     * Always returns that status "Running".
     * @param l Ignored
     * @return Status "Running".
     */
    public Status getStatusSince(long l) {

        //TODO get this to report on the static state
        log.trace("Enter getStatusSince(" + l + ")");

        Status status = new Status();
        StatusMessage statusMessage = new StatusMessage();

        statusMessage.setLogMessage(false);
        statusMessage.setSeverity(Severity.GREEN);
        statusMessage.setTime(System.currentTimeMillis());
        statusMessage.setMessage("Running");
        status.setName(SURVEYABLE_NAME);
        status.getMessages().add(statusMessage);
        return status;
    }

    /**
     * Reports exactly the same as getStatusSince(0L).
     * @return Status.
     */
    public Status getStatus() {
        log.trace("Enter getStatus()");

        return getStatusSince(0L);
    }
}