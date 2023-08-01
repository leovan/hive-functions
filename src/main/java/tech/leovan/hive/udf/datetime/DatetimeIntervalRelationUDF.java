package tech.leovan.hive.udf.datetime;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.sql.Timestamp;

@Description(
        name = "DATETIME_INTERVAL_RELATION",
        value = "_FUNC_(FIRST_START_DATETIME, FIRST_END_DATETIME, SECOND_START_DATETIME, SECOND_END_DATETIME) - 日期时间关系"
)
public class DatetimeIntervalRelationUDF extends UDF {
    private enum DatetimeIntervalRelation {
        PRECEDES("Precedes"),
        PRECEDED_BY("PrecededBy"),
        MEETS("Meets"),
        MET_BY("MetBy"),
        OVERLAPS("Overlaps"),
        OVERLAPPED_BY("OverlappedBy"),
        STARTS("Starts"),
        FINISHES("Finishes"),
        STARTED_BY("StartedBy"),
        FINISHED_BY("FinishedBy"),
        DURING("During"),
        CONTAINS("Contains"),
        EQUALS("Equals"),
        FORMAT_ERROR("FormatError"),
        VALUE_ERROR("ValueError");

        private final String typeName;

        DatetimeIntervalRelation(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return this.typeName;
        }
    }

    private DatetimeIntervalRelation getRelation(
            long firstStartDatetimeTS,
            long firstEndDatetimeTS,
            long secondStartDatetimeTS,
            long secondEndDatetimeTS) {
        if (firstStartDatetimeTS > firstEndDatetimeTS ||
                secondStartDatetimeTS > secondEndDatetimeTS) {
            return DatetimeIntervalRelation.VALUE_ERROR;
        }

        if (firstStartDatetimeTS == secondStartDatetimeTS &&
                firstEndDatetimeTS == secondEndDatetimeTS) {
            return DatetimeIntervalRelation.EQUALS;
        } else if (firstEndDatetimeTS < secondStartDatetimeTS) {
            return DatetimeIntervalRelation.PRECEDES;
        } else if (firstStartDatetimeTS > secondEndDatetimeTS) {
            return DatetimeIntervalRelation.PRECEDED_BY;
        } else if (firstEndDatetimeTS == secondStartDatetimeTS) {
            return DatetimeIntervalRelation.MEETS;
        } else if (firstStartDatetimeTS == secondEndDatetimeTS) {
            return DatetimeIntervalRelation.MET_BY;
        } else if (firstStartDatetimeTS < secondStartDatetimeTS &&
                secondStartDatetimeTS < firstEndDatetimeTS &&
                firstEndDatetimeTS < secondEndDatetimeTS) {
            return DatetimeIntervalRelation.OVERLAPS;
        } else if (secondStartDatetimeTS < firstStartDatetimeTS &&
                firstStartDatetimeTS < secondEndDatetimeTS &&
                secondEndDatetimeTS < firstEndDatetimeTS) {
            return DatetimeIntervalRelation.OVERLAPPED_BY;
        } else if (firstStartDatetimeTS == secondStartDatetimeTS &&
                firstEndDatetimeTS < secondEndDatetimeTS) {
            return DatetimeIntervalRelation.STARTS;
        } else if (firstEndDatetimeTS == secondEndDatetimeTS &&
                secondStartDatetimeTS < firstStartDatetimeTS) {
            return DatetimeIntervalRelation.FINISHES;
        } else if (firstStartDatetimeTS == secondStartDatetimeTS &&
                secondEndDatetimeTS < firstEndDatetimeTS) {
            return DatetimeIntervalRelation.STARTED_BY;
        } else if (firstEndDatetimeTS == secondEndDatetimeTS &&
                firstStartDatetimeTS < secondStartDatetimeTS) {
            return DatetimeIntervalRelation.FINISHED_BY;
        } else if (secondStartDatetimeTS < firstStartDatetimeTS &&
                firstEndDatetimeTS < secondEndDatetimeTS) {
            return DatetimeIntervalRelation.DURING;
        } else if (firstStartDatetimeTS < secondStartDatetimeTS &&
                secondEndDatetimeTS < firstEndDatetimeTS) {
            return DatetimeIntervalRelation.CONTAINS;
        } else {
            return DatetimeIntervalRelation.VALUE_ERROR;
        }
    }

    public String evaluate(
            String firstStartDatetime,
            String firstEndDatetime,
            String secondStartDatetime,
            String secondEndDatetime) {
        if (firstStartDatetime == null ||
                firstEndDatetime == null ||
                secondStartDatetime == null ||
                secondEndDatetime == null) {
            return DatetimeIntervalRelation.VALUE_ERROR.typeName;
        }

        try {
            long firstStartDatetimeTS = Timestamp.valueOf(firstStartDatetime).getTime();
            long firstEndDatetimeTS = Timestamp.valueOf(firstEndDatetime).getTime();
            long secondStartDatetimeTS = Timestamp.valueOf(secondStartDatetime).getTime();
            long secondEndDatetimeTS = Timestamp.valueOf(secondEndDatetime).getTime();

            return getRelation(
                    firstStartDatetimeTS,
                    firstEndDatetimeTS,
                    secondStartDatetimeTS,
                    secondEndDatetimeTS
            ).typeName;
        } catch (IllegalArgumentException e) {
            return DatetimeIntervalRelation.FORMAT_ERROR.typeName;
        } catch (Exception e) {
            return DatetimeIntervalRelation.VALUE_ERROR.typeName;
        }
    }
}
