package grakn.simulation.grakn.action.insight;

import grakn.simulation.common.action.insight.FindLivedInAction;
import grakn.simulation.grakn.driver.GraknOperation;
import graql.lang.Graql;
import graql.lang.query.GraqlGet;

import java.util.List;

import static grakn.simulation.grakn.action.Model.CITY;
import static grakn.simulation.grakn.action.Model.EMAIL;
import static grakn.simulation.grakn.action.Model.LOCATION_NAME;
import static grakn.simulation.grakn.action.Model.PERSON;
import static grakn.simulation.grakn.action.Model.RESIDENCY;
import static grakn.simulation.grakn.action.Model.RESIDENCY_LOCATION;
import static grakn.simulation.grakn.action.Model.RESIDENCY_RESIDENT;

public class GraknFindLivedInAction extends FindLivedInAction<GraknOperation> {
    public GraknFindLivedInAction(GraknOperation dbOperation) {
        super(dbOperation);
    }

    @Override
    public List<String> run() {
        return dbOperation.sortedExecute(query(), EMAIL, null);
    }

    public static GraqlGet.Unfiltered query() {
        return Graql.match(
                    Graql.var(CITY).isa(CITY)
                            .has(LOCATION_NAME, "Berlin"),
                    Graql.var(RESIDENCY).isa(RESIDENCY)
                            .rel(RESIDENCY_LOCATION, Graql.var(CITY))
                            .rel(RESIDENCY_RESIDENT, Graql.var(PERSON)),
                    Graql.var(PERSON).isa(PERSON).has(EMAIL, Graql.var(EMAIL))
            ).get();
    }
}
