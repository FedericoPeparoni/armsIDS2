package ca.ids.abms.modules.mtow;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class AverageMtowFactorCsvExportModel {

    @CsvProperty(mtow = true, precision = 2)
    private Double upperLimit;

    @CsvProperty(value = "Average MTOW Factor", precision = 3)
    private Double averageMtowFactor;

    private FactorClass factorClass;

    public Double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Double getAverageMtowFactor() {
        return averageMtowFactor;
    }

    public void setAverageMtowFactor(Double averageMtowFactor) {
        this.averageMtowFactor = averageMtowFactor;
    }

    public FactorClass getFactorClass() {
        return factorClass;
    }

    public void setFactorClass(FactorClass factorClass) {
        this.factorClass = factorClass;
    }
}
