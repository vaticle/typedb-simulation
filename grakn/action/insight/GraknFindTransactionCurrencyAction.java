package grakn.simulation.grakn.action.insight;

import grakn.simulation.common.action.insight.FindTransactionCurrencyAction;
import grakn.simulation.grakn.driver.GraknOperation;
import graql.lang.Graql;
import graql.lang.query.GraqlGet;

import java.util.List;
import java.util.stream.Collectors;

import static grakn.simulation.grakn.action.Model.CURRENCY;
import static grakn.simulation.grakn.action.Model.TRANSACTION;

public class GraknFindTransactionCurrencyAction extends FindTransactionCurrencyAction<GraknOperation> {
    public GraknFindTransactionCurrencyAction(GraknOperation dbOperation) {
        super(dbOperation);
    }

    @Override
    public List<String> run() {
        return dbOperation.execute(query()).stream().map(ans -> ans.get(CURRENCY).asAttribute().value().toString()).collect(Collectors.toList());
    }

    public static GraqlGet.Unfiltered query() {
        return Graql.match(
                    Graql.var(TRANSACTION).isa(TRANSACTION).has(CURRENCY, Graql.var(CURRENCY))
                    ).get();
    }
}
