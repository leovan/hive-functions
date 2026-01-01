package tech.leovan.hive.udf.datetime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UDFDatetimeIntervalRelationTest {
    private final UDFDatetimeIntervalRelation udf = new UDFDatetimeIntervalRelation();

    @Test
    public void testRelationExceptions() {
        Assertions.assertEquals(
                "FormatError",
                udf.evaluate(
                        "1990/01/01 00:00:00",
                        "1990/01/01 00:00:00",
                        "1990/01/01 00:00:00",
                        "1990/01/01 00:00:00"
                )
        );

        Assertions.assertEquals(
                "ValueError",
                udf.evaluate(
                        "1990-01-01 23:59:59",
                        "1990-01-01 00:00:00",
                        "2000-01-01 00:00:00",
                        "2000-01-01 23:59:59"
                )
        );
    }

    @Test
    public void testRelationNoPoints() {
        Assertions.assertEquals(
                "Precedes",
                udf.evaluate(
                        "1990/01/01 00:00:00",
                        "1990/01/02 00:00:00",
                        "1990/01/03 00:00:00",
                        "1990/01/04 00:00:00",
                        "yyyy/MM/dd HH:mm:ss"
                )
        );
        Assertions.assertEquals(
                "Precedes",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-04 00:00:00"
                )
        );

        Assertions.assertEquals(
                "PrecededBy",
                udf.evaluate(
                        "1990-01-03 00:00:00",
                        "1990-01-04 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );

        Assertions.assertEquals(
                "Meets",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00"
                )
        );

        Assertions.assertEquals(
                "MetBy",
                udf.evaluate(
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );

        Assertions.assertEquals(
                "Starts",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-03 00:00:00"
                )
        );

        Assertions.assertEquals(
                "Finishes",
                udf.evaluate(
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-03 00:00:00"
                )
        );

        Assertions.assertEquals(
                "StartedBy",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );

        Assertions.assertEquals(
                "FinishedBy",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00"
                )
        );

        Assertions.assertEquals(
                "During",
                udf.evaluate(
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-04 00:00:00"
                )
        );

        Assertions.assertEquals(
                "Contains",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-04 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00"
                )
        );

        Assertions.assertEquals(
                "Equals",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );
    }

    @Test
    public void testRelationOnePoint() {
        Assertions.assertEquals(
                "Precedes",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00"
                )
        );
        Assertions.assertEquals(
                "Precedes",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-03 00:00:00"
                )
        );

        Assertions.assertEquals(
                "PrecededBy",
                udf.evaluate(
                        "1990-01-03 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );
        Assertions.assertEquals(
                "PrecededBy",
                udf.evaluate(
                        "1990-01-03 00:00:00",
                        "1990-01-04 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-01 00:00:00"
                )
        );

        Assertions.assertEquals(
                "Meets",
                udf.evaluate(
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00"
                )
        );
        Assertions.assertEquals(
                "Meets",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );

        Assertions.assertEquals(
                "MetBy",
                udf.evaluate(
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );
        Assertions.assertEquals(
                "MetBy",
                udf.evaluate(
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );

        Assertions.assertEquals(
                "During",
                udf.evaluate(
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-04 00:00:00"
                )
        );

        Assertions.assertEquals(
                "Contains",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-04 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );
    }

    @Test
    public void testRelationTwoPoints() {
        Assertions.assertEquals(
                "Precedes",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00"
                )
        );
        Assertions.assertEquals(
                "Precedes",
                udf.evaluate(
                        "1990-01-02 00:00:00",
                        "1990-01-02 00:00:00",
                        "1990-01-03 00:00:00",
                        "1990-01-03 00:00:00"
                )
        );

        Assertions.assertEquals(
                "Equals",
                udf.evaluate(
                        "1990-01-01 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-01 00:00:00",
                        "1990-01-01 00:00:00"
                )
        );
    }
}
