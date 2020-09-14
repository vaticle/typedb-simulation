package grakn.simulation.db.common.agents.interaction;

import grabl.tracing.client.GrablTracingThreadStatic.ThreadTrace;
import grakn.simulation.db.common.agents.base.Agent;
import grakn.simulation.db.common.agents.base.AgentResult;
import grakn.simulation.db.common.agents.base.AgentResultSet;
import grakn.simulation.db.common.agents.base.IterationContext;
import grakn.simulation.db.common.world.World;

import java.time.LocalDateTime;
import java.util.List;

import static grabl.tracing.client.GrablTracingThreadStatic.traceOnThread;
import static grakn.simulation.db.grakn.schema.Schema.MARRIAGE_HUSBAND;
import static grakn.simulation.db.grakn.schema.Schema.MARRIAGE_WIFE;
import static java.util.Collections.shuffle;

public interface MarriageAgentBase extends InteractionAgent<World.City> {

    enum MarriageAgentField implements Agent.ComparableField {
        MARRIAGE_IDENTIFIER, WIFE_EMAIL, HUSBAND_EMAIL, CITY_NAME
    }

    @Override
    default AgentResultSet iterate(Agent<World.City, ?> agent, World.City city, IterationContext iterationContext) {
        AgentResultSet agentResultSet = new AgentResultSet();
        agent.log().message("MarriageAgent", String.format("Simulation step %d", iterationContext.simulationStep()));
        // Find bachelors and bachelorettes who are considered adults and who are not in a marriage and pair them off randomly
        List<String> womenEmails;
        agent.startAction();

        LocalDateTime dobOfAdults = iterationContext.today().minusYears(iterationContext.world().AGE_OF_ADULTHOOD);

        try (ThreadTrace trace = traceOnThread(agent.registerMethodTrace("getSingleWomen"))) {
            womenEmails = getUnmarriedPeopleOfGender("getSingleWomen",city, "female", MARRIAGE_WIFE, dobOfAdults);
        }
        shuffle(womenEmails);

        List<String> menEmails;
        try (ThreadTrace trace = traceOnThread(agent.registerMethodTrace("getSingleMen"))) {
            menEmails = getUnmarriedPeopleOfGender("getSingleMen", city, "male", MARRIAGE_HUSBAND, dobOfAdults);
        }
        shuffle(menEmails);

        int numMarriages = iterationContext.world().getScaleFactor();

        int numMarriagesPossible = Math.min(numMarriages, Math.min(womenEmails.size(), menEmails.size()));

        if (numMarriagesPossible > 0) {
            for (int i = 0; i < numMarriagesPossible; i++) {
                String wifeEmail = womenEmails.get(i);
                String husbandEmail = menEmails.get(i);
                int marriageIdentifier = (wifeEmail + husbandEmail).hashCode();

                String scope = "insertMarriage";
                try (ThreadTrace trace = traceOnThread(agent.checkMethodTrace(scope))) {
                    agentResultSet.add(insertMarriage(scope, city, marriageIdentifier, wifeEmail, husbandEmail));
                }
            }
            agent.commitAction();
        } else {
            agent.stopAction();
        }
        return agentResultSet;
    }

    List<String> getUnmarriedPeopleOfGender(String scope, World.City city, String gender, String marriageRole, LocalDateTime dobOfAdults);

    AgentResult insertMarriage(String scope, World.City city, int marriageIdentifier, String wifeEmail, String husbandEmail);
}
