package grakn.simulation.grakn.action.read;

import grakn.simulation.common.action.read.CompaniesInContinentAction;
import grakn.simulation.common.world.World;
import grakn.simulation.grakn.driver.GraknOperation;
import graql.lang.Graql;
import graql.lang.query.GraqlGet;

import java.util.List;

import static grakn.simulation.grakn.action.Model.COMPANY;
import static grakn.simulation.grakn.action.Model.COMPANY_NUMBER;
import static grakn.simulation.grakn.action.Model.CONTINENT;
import static grakn.simulation.grakn.action.Model.COUNTRY;
import static grakn.simulation.grakn.action.Model.INCORPORATION;
import static grakn.simulation.grakn.action.Model.INCORPORATION_INCORPORATED;
import static grakn.simulation.grakn.action.Model.INCORPORATION_INCORPORATING;
import static grakn.simulation.grakn.action.Model.LOCATION_HIERARCHY;
import static grakn.simulation.grakn.action.Model.LOCATION_NAME;

public class GraknCompaniesInContinentAction extends CompaniesInContinentAction<GraknOperation> {
    public GraknCompaniesInContinentAction(GraknOperation dbOperation, World.Continent continent) {
        super(dbOperation, continent);
    }

    @Override
    public List<Long> run() {
        GraqlGet.Unfiltered query = query(continent.name());
        return dbOperation.sortedExecute(query, COMPANY_NUMBER, null);
    }

    public static GraqlGet.Unfiltered query(String continentName) {
        return Graql.match(
                    Graql.var(CONTINENT).isa(CONTINENT)
                            .has(LOCATION_NAME, continentName),
                    Graql.var(LOCATION_HIERARCHY).isa(LOCATION_HIERARCHY).rel(COUNTRY).rel(CONTINENT),
                    Graql.var(COUNTRY).isa(COUNTRY),
                    Graql.var(COMPANY).isa(COMPANY)
                            .has(COMPANY_NUMBER, Graql.var(COMPANY_NUMBER)),
                    Graql.var(INCORPORATION).isa(INCORPORATION)
                            .rel(INCORPORATION_INCORPORATED, Graql.var(COMPANY))
                            .rel(INCORPORATION_INCORPORATING, Graql.var(COUNTRY))
            ).get();
    }
}
