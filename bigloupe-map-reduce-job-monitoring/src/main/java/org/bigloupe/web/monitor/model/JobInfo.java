package org.bigloupe.web.monitor.model;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.mapred.Counters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Class that encapsulates all information related to a run of a Hadoop MapReduce job. A job might
 * have multiple inputs and outputs, as well as counters, job properties and job data. Job data is
 * metadata about the job run that isn't set in the job properties.
 * @author billg
 */
@SuppressWarnings("deprecation")
@JsonSerialize(
  include=JsonSerialize.Inclusion.NON_NULL
)
public class JobInfo {

  private Map<String, CounterGroupInfo> counterGroupInfoMap;
  private List<InputInfo> inputInfoList;
  private List<OutputInfo> outputInfoList;
  private Map<String, Object> jobData;
  private Properties jobConfProperties;

  public JobInfo(Counters jobCounters, Properties jobConfProperties,
                 List<InputInfo> inputInfoList, List<OutputInfo> outputInfoList) {
    this.jobConfProperties = jobConfProperties;
    this.inputInfoList = inputInfoList;
    this.outputInfoList = outputInfoList;
    counterGroupInfoMap = CounterGroupInfo.counterGroupInfoMap(jobCounters);
  }

  @JsonCreator
  public JobInfo(@JsonProperty("counterGroupInfoMap") Map<String, CounterGroupInfo> counterGroupInfoMap,
                 @JsonProperty("inputInfoList") List<InputInfo> inputInfoList,
                 @JsonProperty("outputInfoList") List<OutputInfo> outputInfoList,
                 @JsonProperty("jobData")  Map<String, Object> jobData,
                 @JsonProperty("jobConfProperties") Properties jobConfProperties) {
    this.counterGroupInfoMap = counterGroupInfoMap;
    this.inputInfoList = inputInfoList;
    this.outputInfoList = outputInfoList;
    this.jobData = jobData;
    this.jobConfProperties = jobConfProperties;
  }

  public List<InputInfo> getInputInfoList() { return inputInfoList; }
  public List<OutputInfo> getOutputInfoList() { return outputInfoList; }

  public Map<String, Object> getJobData() { return jobData; }
  protected void setJobData(Map<String, Object> jobData) { this.jobData = jobData; }

  public Properties getJobConfProperties() { return jobConfProperties; }

  public Map<String, CounterGroupInfo> getCounterGroupInfoMap() { return counterGroupInfoMap; }
  public CounterGroupInfo getCounterGroupInfo(String name) {
    return counterGroupInfoMap == null ? null : counterGroupInfoMap.get(name);
  }

  /**
   * Class that represents information about a data input to a job.
   */
  @JsonSerialize(
    include=JsonSerialize.Inclusion.NON_NULL
  )
  public static class InputInfo {
    private String name;
    private String location;
    private long numberBytes;
    private long numberRecords;
    private boolean successful;
    private String inputType;

    @JsonCreator
    public InputInfo(@JsonProperty("name") String name,
                     @JsonProperty("location") String location,
                     @JsonProperty("numberBytes") long numberBytes,
                     @JsonProperty("numberRecords") long numberRecords,
                     @JsonProperty("successful") boolean successful,
                     @JsonProperty("inputType") String inputType) {
      this.name = name;
      this.location = location;
      this.numberBytes = numberBytes;
      this.numberRecords = numberRecords;
      this.successful = successful;
      this.inputType = inputType;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public long getNumberBytes() { return numberBytes; }
    public long getNumberRecords() { return numberRecords; }
    public boolean isSuccessful() { return successful; }
    public String getInputType() { return inputType; }
  }

  /**
   * Class that represents information about a data output of a job.
   */
  @JsonSerialize(
    include=JsonSerialize.Inclusion.NON_NULL
  )
  public static class OutputInfo {
    private String name;
    private String location;
    private long numberBytes;
    private long numberRecords;
    private boolean successful;
    private String functionName;
    private String alias;

    @JsonCreator
    public OutputInfo(@JsonProperty("name") String name,
                      @JsonProperty("location") String location,
                      @JsonProperty("numberBytes") long numberBytes,
                      @JsonProperty("numberRecords") long numberRecords,
                      @JsonProperty("successful") boolean successful,
                      @JsonProperty("functionName") String functionName,
                      @JsonProperty("alias") String alias) {
      this.name = name;
      this.location = location;
      this.numberBytes = numberBytes;
      this.numberRecords = numberRecords;
      this.successful = successful;
      this.functionName = functionName;
      this.alias = alias;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public long getNumberBytes() { return numberBytes; }
    public long getNumberRecords() { return numberRecords; }
    public boolean isSuccessful() { return successful; }
    public String getFunctionName() { return functionName; }
    public String getAlias() { return alias; }
  }
}
