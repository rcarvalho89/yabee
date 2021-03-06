package br.com.ca.blueocean.vo;

/**
 * InitiativeReportVo
 *
 * Represents the summary of the state of an initiative considering its deliverables.
 *
 * @author Rodrigo Carvalho
 */
public class InitiativeReportVo {

    private String initiativeId = null;
    private double totalNum = 0;
    private double onTimeNum = 0;
    private double lateNum = 0;
    private double lastDayNum = 0;

    /**
     * Constructor
     *
     */
    public InitiativeReportVo(){
    }

    /**
     * Constructor
     *
     * @param initiativeId
     * @param totalNum
     * @param onTimeNum
     * @param lateNum
     * @param lastDayNum
     */
    public InitiativeReportVo(String initiativeId,
            double totalNum,
            double onTimeNum,
            double lateNum,
            double lastDayNum){

        this.initiativeId = initiativeId;
        this.totalNum = totalNum;
        this.onTimeNum = onTimeNum;
        this.lateNum = lateNum;
        this.lastDayNum = lastDayNum;
    }

    /**
     * Getter/Setter Methods
     *
     */

    public String getInitiativeId() {
        return initiativeId;
    }

    public void setInitiativeId(String initiativeId) {
        this.initiativeId = initiativeId;
    }

    public double getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(double totalNum) {
        this.totalNum = totalNum;
    }

    public double getOnTimeNum() {
        return onTimeNum;
    }

    public void setOnTimeNum(double onTimeNum) {
        this.onTimeNum = onTimeNum;
    }

    public double getLateNum() {
        return lateNum;
    }

    public void setLateNum(double lateNum) {
        this.lateNum = lateNum;
    }

    public double getLastDayNum() {
        return lastDayNum;
    }

    public void setLastDayNum(double lastDayNum) {
        this.lastDayNum = lastDayNum;
    }
}
