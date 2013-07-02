// ORM class for YO_DFBS
// WARNING: This class is AUTO-GENERATED. Modify at your own risk.
import java.math.BigDecimal; 
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;
import com.cloudera.sqoop.lib.JdbcWritableBridge;
import com.cloudera.sqoop.lib.DelimiterSet;
import com.cloudera.sqoop.lib.FieldFormatter;
import com.cloudera.sqoop.lib.RecordParser;
import com.cloudera.sqoop.lib.BooleanParser;
import com.cloudera.sqoop.lib.BlobRef;
import com.cloudera.sqoop.lib.ClobRef;
import com.cloudera.sqoop.lib.LargeObjectLoader;
import com.cloudera.sqoop.lib.SqoopRecord;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class YO_DFBS extends SqoopRecord  implements DBWritable, Writable {
  private final int PROTOCOL_VERSION = 3;
  public int getClassFormatVersion() { return PROTOCOL_VERSION; }
  protected ResultSet __cur_result_set;
  private Long DFB_ID;
  public Long get_DFB_ID() {
    return DFB_ID;
  }
  public void set_DFB_ID(Long DFB_ID) {
    this.DFB_ID = DFB_ID;
  }
  public YO_DFBS with_DFB_ID(Long DFB_ID) {
    this.DFB_ID = DFB_ID;
    return this;
  }
  private String BKG_PNR_ID;
  public String get_BKG_PNR_ID() {
    return BKG_PNR_ID;
  }
  public void set_BKG_PNR_ID(String BKG_PNR_ID) {
    this.BKG_PNR_ID = BKG_PNR_ID;
  }
  public YO_DFBS with_BKG_PNR_ID(String BKG_PNR_ID) {
    this.BKG_PNR_ID = BKG_PNR_ID;
    return this;
  }
  private java.sql.Timestamp BKG_CREDATETIME;
  public java.sql.Timestamp get_BKG_CREDATETIME() {
    return BKG_CREDATETIME;
  }
  public void set_BKG_CREDATETIME(java.sql.Timestamp BKG_CREDATETIME) {
    this.BKG_CREDATETIME = BKG_CREDATETIME;
  }
  public YO_DFBS with_BKG_CREDATETIME(java.sql.Timestamp BKG_CREDATETIME) {
    this.BKG_CREDATETIME = BKG_CREDATETIME;
    return this;
  }
  private Long DFLOW_ID;
  public Long get_DFLOW_ID() {
    return DFLOW_ID;
  }
  public void set_DFLOW_ID(Long DFLOW_ID) {
    this.DFLOW_ID = DFLOW_ID;
  }
  public YO_DFBS with_DFLOW_ID(Long DFLOW_ID) {
    this.DFLOW_ID = DFLOW_ID;
    return this;
  }
  private Long TRUEOD_ID;
  public Long get_TRUEOD_ID() {
    return TRUEOD_ID;
  }
  public void set_TRUEOD_ID(Long TRUEOD_ID) {
    this.TRUEOD_ID = TRUEOD_ID;
  }
  public YO_DFBS with_TRUEOD_ID(Long TRUEOD_ID) {
    this.TRUEOD_ID = TRUEOD_ID;
    return this;
  }
  private Integer DENIED_BOARDING;
  public Integer get_DENIED_BOARDING() {
    return DENIED_BOARDING;
  }
  public void set_DENIED_BOARDING(Integer DENIED_BOARDING) {
    this.DENIED_BOARDING = DENIED_BOARDING;
  }
  public YO_DFBS with_DENIED_BOARDING(Integer DENIED_BOARDING) {
    this.DENIED_BOARDING = DENIED_BOARDING;
    return this;
  }
  private Double NOSH_RATE;
  public Double get_NOSH_RATE() {
    return NOSH_RATE;
  }
  public void set_NOSH_RATE(Double NOSH_RATE) {
    this.NOSH_RATE = NOSH_RATE;
  }
  public YO_DFBS with_NOSH_RATE(Double NOSH_RATE) {
    this.NOSH_RATE = NOSH_RATE;
    return this;
  }
  private java.sql.Timestamp INVOL_CHGDAT;
  public java.sql.Timestamp get_INVOL_CHGDAT() {
    return INVOL_CHGDAT;
  }
  public void set_INVOL_CHGDAT(java.sql.Timestamp INVOL_CHGDAT) {
    this.INVOL_CHGDAT = INVOL_CHGDAT;
  }
  public YO_DFBS with_INVOL_CHGDAT(java.sql.Timestamp INVOL_CHGDAT) {
    this.INVOL_CHGDAT = INVOL_CHGDAT;
    return this;
  }
  private java.sql.Timestamp CXLDATETIME;
  public java.sql.Timestamp get_CXLDATETIME() {
    return CXLDATETIME;
  }
  public void set_CXLDATETIME(java.sql.Timestamp CXLDATETIME) {
    this.CXLDATETIME = CXLDATETIME;
  }
  public YO_DFBS with_CXLDATETIME(java.sql.Timestamp CXLDATETIME) {
    this.CXLDATETIME = CXLDATETIME;
    return this;
  }
  private java.sql.Timestamp DEMDATETIME;
  public java.sql.Timestamp get_DEMDATETIME() {
    return DEMDATETIME;
  }
  public void set_DEMDATETIME(java.sql.Timestamp DEMDATETIME) {
    this.DEMDATETIME = DEMDATETIME;
  }
  public YO_DFBS with_DEMDATETIME(java.sql.Timestamp DEMDATETIME) {
    this.DEMDATETIME = DEMDATETIME;
    return this;
  }
  private Integer NB_PAX;
  public Integer get_NB_PAX() {
    return NB_PAX;
  }
  public void set_NB_PAX(Integer NB_PAX) {
    this.NB_PAX = NB_PAX;
  }
  public YO_DFBS with_NB_PAX(Integer NB_PAX) {
    this.NB_PAX = NB_PAX;
    return this;
  }
  private java.sql.Timestamp UPDDATETIME;
  public java.sql.Timestamp get_UPDDATETIME() {
    return UPDDATETIME;
  }
  public void set_UPDDATETIME(java.sql.Timestamp UPDDATETIME) {
    this.UPDDATETIME = UPDDATETIME;
  }
  public YO_DFBS with_UPDDATETIME(java.sql.Timestamp UPDDATETIME) {
    this.UPDDATETIME = UPDDATETIME;
    return this;
  }
  private Integer FLOW_LOWEST_CLS_IND;
  public Integer get_FLOW_LOWEST_CLS_IND() {
    return FLOW_LOWEST_CLS_IND;
  }
  public void set_FLOW_LOWEST_CLS_IND(Integer FLOW_LOWEST_CLS_IND) {
    this.FLOW_LOWEST_CLS_IND = FLOW_LOWEST_CLS_IND;
  }
  public YO_DFBS with_FLOW_LOWEST_CLS_IND(Integer FLOW_LOWEST_CLS_IND) {
    this.FLOW_LOWEST_CLS_IND = FLOW_LOWEST_CLS_IND;
    return this;
  }
  private Integer RNG_LOWEST_CLS_IND;
  public Integer get_RNG_LOWEST_CLS_IND() {
    return RNG_LOWEST_CLS_IND;
  }
  public void set_RNG_LOWEST_CLS_IND(Integer RNG_LOWEST_CLS_IND) {
    this.RNG_LOWEST_CLS_IND = RNG_LOWEST_CLS_IND;
  }
  public YO_DFBS with_RNG_LOWEST_CLS_IND(Integer RNG_LOWEST_CLS_IND) {
    this.RNG_LOWEST_CLS_IND = RNG_LOWEST_CLS_IND;
    return this;
  }
  private java.sql.Timestamp TKT_DATETIME;
  public java.sql.Timestamp get_TKT_DATETIME() {
    return TKT_DATETIME;
  }
  public void set_TKT_DATETIME(java.sql.Timestamp TKT_DATETIME) {
    this.TKT_DATETIME = TKT_DATETIME;
  }
  public YO_DFBS with_TKT_DATETIME(java.sql.Timestamp TKT_DATETIME) {
    this.TKT_DATETIME = TKT_DATETIME;
    return this;
  }
  private Integer GHOST_BKG;
  public Integer get_GHOST_BKG() {
    return GHOST_BKG;
  }
  public void set_GHOST_BKG(Integer GHOST_BKG) {
    this.GHOST_BKG = GHOST_BKG;
  }
  public YO_DFBS with_GHOST_BKG(Integer GHOST_BKG) {
    this.GHOST_BKG = GHOST_BKG;
    return this;
  }
  private Integer MASTER_IND;
  public Integer get_MASTER_IND() {
    return MASTER_IND;
  }
  public void set_MASTER_IND(Integer MASTER_IND) {
    this.MASTER_IND = MASTER_IND;
  }
  public YO_DFBS with_MASTER_IND(Integer MASTER_IND) {
    this.MASTER_IND = MASTER_IND;
    return this;
  }
  private java.sql.Timestamp FINALIZATION_DAT;
  public java.sql.Timestamp get_FINALIZATION_DAT() {
    return FINALIZATION_DAT;
  }
  public void set_FINALIZATION_DAT(java.sql.Timestamp FINALIZATION_DAT) {
    this.FINALIZATION_DAT = FINALIZATION_DAT;
  }
  public YO_DFBS with_FINALIZATION_DAT(java.sql.Timestamp FINALIZATION_DAT) {
    this.FINALIZATION_DAT = FINALIZATION_DAT;
    return this;
  }
  private Integer REG_NOSH;
  public Integer get_REG_NOSH() {
    return REG_NOSH;
  }
  public void set_REG_NOSH(Integer REG_NOSH) {
    this.REG_NOSH = REG_NOSH;
  }
  public YO_DFBS with_REG_NOSH(Integer REG_NOSH) {
    this.REG_NOSH = REG_NOSH;
    return this;
  }
  private Integer REG_GOSH;
  public Integer get_REG_GOSH() {
    return REG_GOSH;
  }
  public void set_REG_GOSH(Integer REG_GOSH) {
    this.REG_GOSH = REG_GOSH;
  }
  public YO_DFBS with_REG_GOSH(Integer REG_GOSH) {
    this.REG_GOSH = REG_GOSH;
    return this;
  }
  private Integer SLID_NOSH;
  public Integer get_SLID_NOSH() {
    return SLID_NOSH;
  }
  public void set_SLID_NOSH(Integer SLID_NOSH) {
    this.SLID_NOSH = SLID_NOSH;
  }
  public YO_DFBS with_SLID_NOSH(Integer SLID_NOSH) {
    this.SLID_NOSH = SLID_NOSH;
    return this;
  }
  private Integer SLID_GOSH;
  public Integer get_SLID_GOSH() {
    return SLID_GOSH;
  }
  public void set_SLID_GOSH(Integer SLID_GOSH) {
    this.SLID_GOSH = SLID_GOSH;
  }
  public YO_DFBS with_SLID_GOSH(Integer SLID_GOSH) {
    this.SLID_GOSH = SLID_GOSH;
    return this;
  }
  private java.sql.Timestamp SHUP_RATE_UPDDATETIME;
  public java.sql.Timestamp get_SHUP_RATE_UPDDATETIME() {
    return SHUP_RATE_UPDDATETIME;
  }
  public void set_SHUP_RATE_UPDDATETIME(java.sql.Timestamp SHUP_RATE_UPDDATETIME) {
    this.SHUP_RATE_UPDDATETIME = SHUP_RATE_UPDDATETIME;
  }
  public YO_DFBS with_SHUP_RATE_UPDDATETIME(java.sql.Timestamp SHUP_RATE_UPDDATETIME) {
    this.SHUP_RATE_UPDDATETIME = SHUP_RATE_UPDDATETIME;
    return this;
  }
  private String CLASS_COMBI;
  public String get_CLASS_COMBI() {
    return CLASS_COMBI;
  }
  public void set_CLASS_COMBI(String CLASS_COMBI) {
    this.CLASS_COMBI = CLASS_COMBI;
  }
  public YO_DFBS with_CLASS_COMBI(String CLASS_COMBI) {
    this.CLASS_COMBI = CLASS_COMBI;
    return this;
  }
  private java.sql.Timestamp DEPDAT;
  public java.sql.Timestamp get_DEPDAT() {
    return DEPDAT;
  }
  public void set_DEPDAT(java.sql.Timestamp DEPDAT) {
    this.DEPDAT = DEPDAT;
  }
  public YO_DFBS with_DEPDAT(java.sql.Timestamp DEPDAT) {
    this.DEPDAT = DEPDAT;
    return this;
  }
  private String POS;
  public String get_POS() {
    return POS;
  }
  public void set_POS(String POS) {
    this.POS = POS;
  }
  public YO_DFBS with_POS(String POS) {
    this.POS = POS;
    return this;
  }
  private Double CXLRATE;
  public Double get_CXLRATE() {
    return CXLRATE;
  }
  public void set_CXLRATE(Double CXLRATE) {
    this.CXLRATE = CXLRATE;
  }
  public YO_DFBS with_CXLRATE(Double CXLRATE) {
    this.CXLRATE = CXLRATE;
    return this;
  }
  private Integer NB_NAMES;
  public Integer get_NB_NAMES() {
    return NB_NAMES;
  }
  public void set_NB_NAMES(Integer NB_NAMES) {
    this.NB_NAMES = NB_NAMES;
  }
  public YO_DFBS with_NB_NAMES(Integer NB_NAMES) {
    this.NB_NAMES = NB_NAMES;
    return this;
  }
  private Double PCT_NAMES;
  public Double get_PCT_NAMES() {
    return PCT_NAMES;
  }
  public void set_PCT_NAMES(Double PCT_NAMES) {
    this.PCT_NAMES = PCT_NAMES;
  }
  public YO_DFBS with_PCT_NAMES(Double PCT_NAMES) {
    this.PCT_NAMES = PCT_NAMES;
    return this;
  }
  private Integer SSCAP_CHECK_IND;
  public Integer get_SSCAP_CHECK_IND() {
    return SSCAP_CHECK_IND;
  }
  public void set_SSCAP_CHECK_IND(Integer SSCAP_CHECK_IND) {
    this.SSCAP_CHECK_IND = SSCAP_CHECK_IND;
  }
  public YO_DFBS with_SSCAP_CHECK_IND(Integer SSCAP_CHECK_IND) {
    this.SSCAP_CHECK_IND = SSCAP_CHECK_IND;
    return this;
  }
  private Integer REG_SH;
  public Integer get_REG_SH() {
    return REG_SH;
  }
  public void set_REG_SH(Integer REG_SH) {
    this.REG_SH = REG_SH;
  }
  public YO_DFBS with_REG_SH(Integer REG_SH) {
    this.REG_SH = REG_SH;
    return this;
  }
  private Double SHUP_RATE;
  public Double get_SHUP_RATE() {
    return SHUP_RATE;
  }
  public void set_SHUP_RATE(Double SHUP_RATE) {
    this.SHUP_RATE = SHUP_RATE;
  }
  public YO_DFBS with_SHUP_RATE(Double SHUP_RATE) {
    this.SHUP_RATE = SHUP_RATE;
    return this;
  }
  private Integer IS_LOCAL;
  public Integer get_IS_LOCAL() {
    return IS_LOCAL;
  }
  public void set_IS_LOCAL(Integer IS_LOCAL) {
    this.IS_LOCAL = IS_LOCAL;
  }
  public YO_DFBS with_IS_LOCAL(Integer IS_LOCAL) {
    this.IS_LOCAL = IS_LOCAL;
    return this;
  }
  private java.sql.Timestamp OUTBOUND_FLOW_DAT;
  public java.sql.Timestamp get_OUTBOUND_FLOW_DAT() {
    return OUTBOUND_FLOW_DAT;
  }
  public void set_OUTBOUND_FLOW_DAT(java.sql.Timestamp OUTBOUND_FLOW_DAT) {
    this.OUTBOUND_FLOW_DAT = OUTBOUND_FLOW_DAT;
  }
  public YO_DFBS with_OUTBOUND_FLOW_DAT(java.sql.Timestamp OUTBOUND_FLOW_DAT) {
    this.OUTBOUND_FLOW_DAT = OUTBOUND_FLOW_DAT;
    return this;
  }
  private Integer GRP_IND;
  public Integer get_GRP_IND() {
    return GRP_IND;
  }
  public void set_GRP_IND(Integer GRP_IND) {
    this.GRP_IND = GRP_IND;
  }
  public YO_DFBS with_GRP_IND(Integer GRP_IND) {
    this.GRP_IND = GRP_IND;
    return this;
  }
  private Integer BOARDED;
  public Integer get_BOARDED() {
    return BOARDED;
  }
  public void set_BOARDED(Integer BOARDED) {
    this.BOARDED = BOARDED;
  }
  public YO_DFBS with_BOARDED(Integer BOARDED) {
    this.BOARDED = BOARDED;
    return this;
  }
  private String CAB_LVL_COMBI;
  public String get_CAB_LVL_COMBI() {
    return CAB_LVL_COMBI;
  }
  public void set_CAB_LVL_COMBI(String CAB_LVL_COMBI) {
    this.CAB_LVL_COMBI = CAB_LVL_COMBI;
  }
  public YO_DFBS with_CAB_LVL_COMBI(String CAB_LVL_COMBI) {
    this.CAB_LVL_COMBI = CAB_LVL_COMBI;
    return this;
  }
  private String POO;
  public String get_POO() {
    return POO;
  }
  public void set_POO(String POO) {
    this.POO = POO;
  }
  public YO_DFBS with_POO(String POO) {
    this.POO = POO;
    return this;
  }
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof YO_DFBS)) {
      return false;
    }
    YO_DFBS that = (YO_DFBS) o;
    boolean equal = true;
    equal = equal && (this.DFB_ID == null ? that.DFB_ID == null : this.DFB_ID.equals(that.DFB_ID));
    equal = equal && (this.BKG_PNR_ID == null ? that.BKG_PNR_ID == null : this.BKG_PNR_ID.equals(that.BKG_PNR_ID));
    equal = equal && (this.BKG_CREDATETIME == null ? that.BKG_CREDATETIME == null : this.BKG_CREDATETIME.equals(that.BKG_CREDATETIME));
    equal = equal && (this.DFLOW_ID == null ? that.DFLOW_ID == null : this.DFLOW_ID.equals(that.DFLOW_ID));
    equal = equal && (this.TRUEOD_ID == null ? that.TRUEOD_ID == null : this.TRUEOD_ID.equals(that.TRUEOD_ID));
    equal = equal && (this.DENIED_BOARDING == null ? that.DENIED_BOARDING == null : this.DENIED_BOARDING.equals(that.DENIED_BOARDING));
    equal = equal && (this.NOSH_RATE == null ? that.NOSH_RATE == null : this.NOSH_RATE.equals(that.NOSH_RATE));
    equal = equal && (this.INVOL_CHGDAT == null ? that.INVOL_CHGDAT == null : this.INVOL_CHGDAT.equals(that.INVOL_CHGDAT));
    equal = equal && (this.CXLDATETIME == null ? that.CXLDATETIME == null : this.CXLDATETIME.equals(that.CXLDATETIME));
    equal = equal && (this.DEMDATETIME == null ? that.DEMDATETIME == null : this.DEMDATETIME.equals(that.DEMDATETIME));
    equal = equal && (this.NB_PAX == null ? that.NB_PAX == null : this.NB_PAX.equals(that.NB_PAX));
    equal = equal && (this.UPDDATETIME == null ? that.UPDDATETIME == null : this.UPDDATETIME.equals(that.UPDDATETIME));
    equal = equal && (this.FLOW_LOWEST_CLS_IND == null ? that.FLOW_LOWEST_CLS_IND == null : this.FLOW_LOWEST_CLS_IND.equals(that.FLOW_LOWEST_CLS_IND));
    equal = equal && (this.RNG_LOWEST_CLS_IND == null ? that.RNG_LOWEST_CLS_IND == null : this.RNG_LOWEST_CLS_IND.equals(that.RNG_LOWEST_CLS_IND));
    equal = equal && (this.TKT_DATETIME == null ? that.TKT_DATETIME == null : this.TKT_DATETIME.equals(that.TKT_DATETIME));
    equal = equal && (this.GHOST_BKG == null ? that.GHOST_BKG == null : this.GHOST_BKG.equals(that.GHOST_BKG));
    equal = equal && (this.MASTER_IND == null ? that.MASTER_IND == null : this.MASTER_IND.equals(that.MASTER_IND));
    equal = equal && (this.FINALIZATION_DAT == null ? that.FINALIZATION_DAT == null : this.FINALIZATION_DAT.equals(that.FINALIZATION_DAT));
    equal = equal && (this.REG_NOSH == null ? that.REG_NOSH == null : this.REG_NOSH.equals(that.REG_NOSH));
    equal = equal && (this.REG_GOSH == null ? that.REG_GOSH == null : this.REG_GOSH.equals(that.REG_GOSH));
    equal = equal && (this.SLID_NOSH == null ? that.SLID_NOSH == null : this.SLID_NOSH.equals(that.SLID_NOSH));
    equal = equal && (this.SLID_GOSH == null ? that.SLID_GOSH == null : this.SLID_GOSH.equals(that.SLID_GOSH));
    equal = equal && (this.SHUP_RATE_UPDDATETIME == null ? that.SHUP_RATE_UPDDATETIME == null : this.SHUP_RATE_UPDDATETIME.equals(that.SHUP_RATE_UPDDATETIME));
    equal = equal && (this.CLASS_COMBI == null ? that.CLASS_COMBI == null : this.CLASS_COMBI.equals(that.CLASS_COMBI));
    equal = equal && (this.DEPDAT == null ? that.DEPDAT == null : this.DEPDAT.equals(that.DEPDAT));
    equal = equal && (this.POS == null ? that.POS == null : this.POS.equals(that.POS));
    equal = equal && (this.CXLRATE == null ? that.CXLRATE == null : this.CXLRATE.equals(that.CXLRATE));
    equal = equal && (this.NB_NAMES == null ? that.NB_NAMES == null : this.NB_NAMES.equals(that.NB_NAMES));
    equal = equal && (this.PCT_NAMES == null ? that.PCT_NAMES == null : this.PCT_NAMES.equals(that.PCT_NAMES));
    equal = equal && (this.SSCAP_CHECK_IND == null ? that.SSCAP_CHECK_IND == null : this.SSCAP_CHECK_IND.equals(that.SSCAP_CHECK_IND));
    equal = equal && (this.REG_SH == null ? that.REG_SH == null : this.REG_SH.equals(that.REG_SH));
    equal = equal && (this.SHUP_RATE == null ? that.SHUP_RATE == null : this.SHUP_RATE.equals(that.SHUP_RATE));
    equal = equal && (this.IS_LOCAL == null ? that.IS_LOCAL == null : this.IS_LOCAL.equals(that.IS_LOCAL));
    equal = equal && (this.OUTBOUND_FLOW_DAT == null ? that.OUTBOUND_FLOW_DAT == null : this.OUTBOUND_FLOW_DAT.equals(that.OUTBOUND_FLOW_DAT));
    equal = equal && (this.GRP_IND == null ? that.GRP_IND == null : this.GRP_IND.equals(that.GRP_IND));
    equal = equal && (this.BOARDED == null ? that.BOARDED == null : this.BOARDED.equals(that.BOARDED));
    equal = equal && (this.CAB_LVL_COMBI == null ? that.CAB_LVL_COMBI == null : this.CAB_LVL_COMBI.equals(that.CAB_LVL_COMBI));
    equal = equal && (this.POO == null ? that.POO == null : this.POO.equals(that.POO));
    return equal;
  }
  public void readFields(ResultSet __dbResults) throws SQLException {
    this.__cur_result_set = __dbResults;
    this.DFB_ID = JdbcWritableBridge.readLong(1, __dbResults);
    this.BKG_PNR_ID = JdbcWritableBridge.readString(2, __dbResults);
    this.BKG_CREDATETIME = JdbcWritableBridge.readTimestamp(3, __dbResults);
    this.DFLOW_ID = JdbcWritableBridge.readLong(4, __dbResults);
    this.TRUEOD_ID = JdbcWritableBridge.readLong(5, __dbResults);
    this.DENIED_BOARDING = JdbcWritableBridge.readInteger(6, __dbResults);
    this.NOSH_RATE = JdbcWritableBridge.readDouble(7, __dbResults);
    this.INVOL_CHGDAT = JdbcWritableBridge.readTimestamp(8, __dbResults);
    this.CXLDATETIME = JdbcWritableBridge.readTimestamp(9, __dbResults);
    this.DEMDATETIME = JdbcWritableBridge.readTimestamp(10, __dbResults);
    this.NB_PAX = JdbcWritableBridge.readInteger(11, __dbResults);
    this.UPDDATETIME = JdbcWritableBridge.readTimestamp(12, __dbResults);
    this.FLOW_LOWEST_CLS_IND = JdbcWritableBridge.readInteger(13, __dbResults);
    this.RNG_LOWEST_CLS_IND = JdbcWritableBridge.readInteger(14, __dbResults);
    this.TKT_DATETIME = JdbcWritableBridge.readTimestamp(15, __dbResults);
    this.GHOST_BKG = JdbcWritableBridge.readInteger(16, __dbResults);
    this.MASTER_IND = JdbcWritableBridge.readInteger(17, __dbResults);
    this.FINALIZATION_DAT = JdbcWritableBridge.readTimestamp(18, __dbResults);
    this.REG_NOSH = JdbcWritableBridge.readInteger(19, __dbResults);
    this.REG_GOSH = JdbcWritableBridge.readInteger(20, __dbResults);
    this.SLID_NOSH = JdbcWritableBridge.readInteger(21, __dbResults);
    this.SLID_GOSH = JdbcWritableBridge.readInteger(22, __dbResults);
    this.SHUP_RATE_UPDDATETIME = JdbcWritableBridge.readTimestamp(23, __dbResults);
    this.CLASS_COMBI = JdbcWritableBridge.readString(24, __dbResults);
    this.DEPDAT = JdbcWritableBridge.readTimestamp(25, __dbResults);
    this.POS = JdbcWritableBridge.readString(26, __dbResults);
    this.CXLRATE = JdbcWritableBridge.readDouble(27, __dbResults);
    this.NB_NAMES = JdbcWritableBridge.readInteger(28, __dbResults);
    this.PCT_NAMES = JdbcWritableBridge.readDouble(29, __dbResults);
    this.SSCAP_CHECK_IND = JdbcWritableBridge.readInteger(30, __dbResults);
    this.REG_SH = JdbcWritableBridge.readInteger(31, __dbResults);
    this.SHUP_RATE = JdbcWritableBridge.readDouble(32, __dbResults);
    this.IS_LOCAL = JdbcWritableBridge.readInteger(33, __dbResults);
    this.OUTBOUND_FLOW_DAT = JdbcWritableBridge.readTimestamp(34, __dbResults);
    this.GRP_IND = JdbcWritableBridge.readInteger(35, __dbResults);
    this.BOARDED = JdbcWritableBridge.readInteger(36, __dbResults);
    this.CAB_LVL_COMBI = JdbcWritableBridge.readString(37, __dbResults);
    this.POO = JdbcWritableBridge.readString(38, __dbResults);
  }
  public void loadLargeObjects(LargeObjectLoader __loader)
      throws SQLException, IOException, InterruptedException {
  }
  public void write(PreparedStatement __dbStmt) throws SQLException {
    write(__dbStmt, 0);
  }

  public int write(PreparedStatement __dbStmt, int __off) throws SQLException {
    JdbcWritableBridge.writeLong(DFB_ID, 1 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeString(BKG_PNR_ID, 2 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeTimestamp(BKG_CREDATETIME, 3 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeLong(DFLOW_ID, 4 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeLong(TRUEOD_ID, 5 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(DENIED_BOARDING, 6 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeDouble(NOSH_RATE, 7 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeTimestamp(INVOL_CHGDAT, 8 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeTimestamp(CXLDATETIME, 9 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeTimestamp(DEMDATETIME, 10 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeInteger(NB_PAX, 11 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeTimestamp(UPDDATETIME, 12 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeInteger(FLOW_LOWEST_CLS_IND, 13 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(RNG_LOWEST_CLS_IND, 14 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeTimestamp(TKT_DATETIME, 15 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeInteger(GHOST_BKG, 16 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(MASTER_IND, 17 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeTimestamp(FINALIZATION_DAT, 18 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeInteger(REG_NOSH, 19 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(REG_GOSH, 20 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(SLID_NOSH, 21 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(SLID_GOSH, 22 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeTimestamp(SHUP_RATE_UPDDATETIME, 23 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeString(CLASS_COMBI, 24 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeTimestamp(DEPDAT, 25 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeString(POS, 26 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeDouble(CXLRATE, 27 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(NB_NAMES, 28 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeDouble(PCT_NAMES, 29 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(SSCAP_CHECK_IND, 30 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(REG_SH, 31 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeDouble(SHUP_RATE, 32 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(IS_LOCAL, 33 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeTimestamp(OUTBOUND_FLOW_DAT, 34 + __off, 93, __dbStmt);
    JdbcWritableBridge.writeInteger(GRP_IND, 35 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeInteger(BOARDED, 36 + __off, 2, __dbStmt);
    JdbcWritableBridge.writeString(CAB_LVL_COMBI, 37 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(POO, 38 + __off, 12, __dbStmt);
    return 38;
  }
  public void readFields(DataInput __dataIn) throws IOException {
    if (__dataIn.readBoolean()) { 
        this.DFB_ID = null;
    } else {
    this.DFB_ID = Long.valueOf(__dataIn.readLong());
    }
    if (__dataIn.readBoolean()) { 
        this.BKG_PNR_ID = null;
    } else {
    this.BKG_PNR_ID = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.BKG_CREDATETIME = null;
    } else {
    this.BKG_CREDATETIME = new Timestamp(__dataIn.readLong());
    this.BKG_CREDATETIME.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.DFLOW_ID = null;
    } else {
    this.DFLOW_ID = Long.valueOf(__dataIn.readLong());
    }
    if (__dataIn.readBoolean()) { 
        this.TRUEOD_ID = null;
    } else {
    this.TRUEOD_ID = Long.valueOf(__dataIn.readLong());
    }
    if (__dataIn.readBoolean()) { 
        this.DENIED_BOARDING = null;
    } else {
    this.DENIED_BOARDING = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.NOSH_RATE = null;
    } else {
    this.NOSH_RATE = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.INVOL_CHGDAT = null;
    } else {
    this.INVOL_CHGDAT = new Timestamp(__dataIn.readLong());
    this.INVOL_CHGDAT.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.CXLDATETIME = null;
    } else {
    this.CXLDATETIME = new Timestamp(__dataIn.readLong());
    this.CXLDATETIME.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.DEMDATETIME = null;
    } else {
    this.DEMDATETIME = new Timestamp(__dataIn.readLong());
    this.DEMDATETIME.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.NB_PAX = null;
    } else {
    this.NB_PAX = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.UPDDATETIME = null;
    } else {
    this.UPDDATETIME = new Timestamp(__dataIn.readLong());
    this.UPDDATETIME.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.FLOW_LOWEST_CLS_IND = null;
    } else {
    this.FLOW_LOWEST_CLS_IND = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.RNG_LOWEST_CLS_IND = null;
    } else {
    this.RNG_LOWEST_CLS_IND = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.TKT_DATETIME = null;
    } else {
    this.TKT_DATETIME = new Timestamp(__dataIn.readLong());
    this.TKT_DATETIME.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.GHOST_BKG = null;
    } else {
    this.GHOST_BKG = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.MASTER_IND = null;
    } else {
    this.MASTER_IND = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.FINALIZATION_DAT = null;
    } else {
    this.FINALIZATION_DAT = new Timestamp(__dataIn.readLong());
    this.FINALIZATION_DAT.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.REG_NOSH = null;
    } else {
    this.REG_NOSH = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.REG_GOSH = null;
    } else {
    this.REG_GOSH = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.SLID_NOSH = null;
    } else {
    this.SLID_NOSH = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.SLID_GOSH = null;
    } else {
    this.SLID_GOSH = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.SHUP_RATE_UPDDATETIME = null;
    } else {
    this.SHUP_RATE_UPDDATETIME = new Timestamp(__dataIn.readLong());
    this.SHUP_RATE_UPDDATETIME.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.CLASS_COMBI = null;
    } else {
    this.CLASS_COMBI = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.DEPDAT = null;
    } else {
    this.DEPDAT = new Timestamp(__dataIn.readLong());
    this.DEPDAT.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.POS = null;
    } else {
    this.POS = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.CXLRATE = null;
    } else {
    this.CXLRATE = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.NB_NAMES = null;
    } else {
    this.NB_NAMES = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.PCT_NAMES = null;
    } else {
    this.PCT_NAMES = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.SSCAP_CHECK_IND = null;
    } else {
    this.SSCAP_CHECK_IND = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.REG_SH = null;
    } else {
    this.REG_SH = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.SHUP_RATE = null;
    } else {
    this.SHUP_RATE = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.IS_LOCAL = null;
    } else {
    this.IS_LOCAL = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.OUTBOUND_FLOW_DAT = null;
    } else {
    this.OUTBOUND_FLOW_DAT = new Timestamp(__dataIn.readLong());
    this.OUTBOUND_FLOW_DAT.setNanos(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.GRP_IND = null;
    } else {
    this.GRP_IND = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.BOARDED = null;
    } else {
    this.BOARDED = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.CAB_LVL_COMBI = null;
    } else {
    this.CAB_LVL_COMBI = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.POO = null;
    } else {
    this.POO = Text.readString(__dataIn);
    }
  }
  public void write(DataOutput __dataOut) throws IOException {
    if (null == this.DFB_ID) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.DFB_ID);
    }
    if (null == this.BKG_PNR_ID) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, BKG_PNR_ID);
    }
    if (null == this.BKG_CREDATETIME) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.BKG_CREDATETIME.getTime());
    __dataOut.writeInt(this.BKG_CREDATETIME.getNanos());
    }
    if (null == this.DFLOW_ID) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.DFLOW_ID);
    }
    if (null == this.TRUEOD_ID) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.TRUEOD_ID);
    }
    if (null == this.DENIED_BOARDING) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.DENIED_BOARDING);
    }
    if (null == this.NOSH_RATE) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.NOSH_RATE);
    }
    if (null == this.INVOL_CHGDAT) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.INVOL_CHGDAT.getTime());
    __dataOut.writeInt(this.INVOL_CHGDAT.getNanos());
    }
    if (null == this.CXLDATETIME) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.CXLDATETIME.getTime());
    __dataOut.writeInt(this.CXLDATETIME.getNanos());
    }
    if (null == this.DEMDATETIME) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.DEMDATETIME.getTime());
    __dataOut.writeInt(this.DEMDATETIME.getNanos());
    }
    if (null == this.NB_PAX) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.NB_PAX);
    }
    if (null == this.UPDDATETIME) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.UPDDATETIME.getTime());
    __dataOut.writeInt(this.UPDDATETIME.getNanos());
    }
    if (null == this.FLOW_LOWEST_CLS_IND) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.FLOW_LOWEST_CLS_IND);
    }
    if (null == this.RNG_LOWEST_CLS_IND) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.RNG_LOWEST_CLS_IND);
    }
    if (null == this.TKT_DATETIME) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.TKT_DATETIME.getTime());
    __dataOut.writeInt(this.TKT_DATETIME.getNanos());
    }
    if (null == this.GHOST_BKG) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.GHOST_BKG);
    }
    if (null == this.MASTER_IND) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.MASTER_IND);
    }
    if (null == this.FINALIZATION_DAT) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.FINALIZATION_DAT.getTime());
    __dataOut.writeInt(this.FINALIZATION_DAT.getNanos());
    }
    if (null == this.REG_NOSH) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.REG_NOSH);
    }
    if (null == this.REG_GOSH) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.REG_GOSH);
    }
    if (null == this.SLID_NOSH) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.SLID_NOSH);
    }
    if (null == this.SLID_GOSH) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.SLID_GOSH);
    }
    if (null == this.SHUP_RATE_UPDDATETIME) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.SHUP_RATE_UPDDATETIME.getTime());
    __dataOut.writeInt(this.SHUP_RATE_UPDDATETIME.getNanos());
    }
    if (null == this.CLASS_COMBI) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, CLASS_COMBI);
    }
    if (null == this.DEPDAT) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.DEPDAT.getTime());
    __dataOut.writeInt(this.DEPDAT.getNanos());
    }
    if (null == this.POS) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, POS);
    }
    if (null == this.CXLRATE) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.CXLRATE);
    }
    if (null == this.NB_NAMES) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.NB_NAMES);
    }
    if (null == this.PCT_NAMES) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.PCT_NAMES);
    }
    if (null == this.SSCAP_CHECK_IND) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.SSCAP_CHECK_IND);
    }
    if (null == this.REG_SH) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.REG_SH);
    }
    if (null == this.SHUP_RATE) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.SHUP_RATE);
    }
    if (null == this.IS_LOCAL) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.IS_LOCAL);
    }
    if (null == this.OUTBOUND_FLOW_DAT) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeLong(this.OUTBOUND_FLOW_DAT.getTime());
    __dataOut.writeInt(this.OUTBOUND_FLOW_DAT.getNanos());
    }
    if (null == this.GRP_IND) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.GRP_IND);
    }
    if (null == this.BOARDED) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.BOARDED);
    }
    if (null == this.CAB_LVL_COMBI) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, CAB_LVL_COMBI);
    }
    if (null == this.POO) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, POO);
    }
  }
  private final DelimiterSet __outputDelimiters = new DelimiterSet((char) 44, (char) 10, (char) 0, (char) 0, false);
  public String toString() {
    return toString(__outputDelimiters, true);
  }
  public String toString(DelimiterSet delimiters) {
    return toString(delimiters, true);
  }
  public String toString(boolean useRecordDelim) {
    return toString(__outputDelimiters, useRecordDelim);
  }
  public String toString(DelimiterSet delimiters, boolean useRecordDelim) {
    StringBuilder __sb = new StringBuilder();
    char fieldDelim = delimiters.getFieldsTerminatedBy();
    __sb.append(FieldFormatter.escapeAndEnclose(DFB_ID==null?"null":"" + DFB_ID, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(BKG_PNR_ID==null?"null":BKG_PNR_ID, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(BKG_CREDATETIME==null?"null":"" + BKG_CREDATETIME, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(DFLOW_ID==null?"null":"" + DFLOW_ID, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(TRUEOD_ID==null?"null":"" + TRUEOD_ID, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(DENIED_BOARDING==null?"null":"" + DENIED_BOARDING, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(NOSH_RATE==null?"null":"" + NOSH_RATE, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(INVOL_CHGDAT==null?"null":"" + INVOL_CHGDAT, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(CXLDATETIME==null?"null":"" + CXLDATETIME, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(DEMDATETIME==null?"null":"" + DEMDATETIME, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(NB_PAX==null?"null":"" + NB_PAX, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(UPDDATETIME==null?"null":"" + UPDDATETIME, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(FLOW_LOWEST_CLS_IND==null?"null":"" + FLOW_LOWEST_CLS_IND, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(RNG_LOWEST_CLS_IND==null?"null":"" + RNG_LOWEST_CLS_IND, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(TKT_DATETIME==null?"null":"" + TKT_DATETIME, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(GHOST_BKG==null?"null":"" + GHOST_BKG, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(MASTER_IND==null?"null":"" + MASTER_IND, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(FINALIZATION_DAT==null?"null":"" + FINALIZATION_DAT, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(REG_NOSH==null?"null":"" + REG_NOSH, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(REG_GOSH==null?"null":"" + REG_GOSH, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(SLID_NOSH==null?"null":"" + SLID_NOSH, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(SLID_GOSH==null?"null":"" + SLID_GOSH, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(SHUP_RATE_UPDDATETIME==null?"null":"" + SHUP_RATE_UPDDATETIME, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(CLASS_COMBI==null?"null":CLASS_COMBI, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(DEPDAT==null?"null":"" + DEPDAT, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(POS==null?"null":POS, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(CXLRATE==null?"null":"" + CXLRATE, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(NB_NAMES==null?"null":"" + NB_NAMES, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(PCT_NAMES==null?"null":"" + PCT_NAMES, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(SSCAP_CHECK_IND==null?"null":"" + SSCAP_CHECK_IND, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(REG_SH==null?"null":"" + REG_SH, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(SHUP_RATE==null?"null":"" + SHUP_RATE, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(IS_LOCAL==null?"null":"" + IS_LOCAL, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(OUTBOUND_FLOW_DAT==null?"null":"" + OUTBOUND_FLOW_DAT, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(GRP_IND==null?"null":"" + GRP_IND, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(BOARDED==null?"null":"" + BOARDED, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(CAB_LVL_COMBI==null?"null":CAB_LVL_COMBI, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(POO==null?"null":POO, delimiters));
    if (useRecordDelim) {
      __sb.append(delimiters.getLinesTerminatedBy());
    }
    return __sb.toString();
  }
  private final DelimiterSet __inputDelimiters = new DelimiterSet((char) 44, (char) 10, (char) 0, (char) 0, false);
  private RecordParser __parser;
  public void parse(Text __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(CharSequence __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(byte [] __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(char [] __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(ByteBuffer __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(CharBuffer __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  private void __loadFromFields(List<String> fields) {
    Iterator<String> __it = fields.listIterator();
    String __cur_str;
    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.DFB_ID = null; } else {
      this.DFB_ID = Long.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.BKG_PNR_ID = null; } else {
      this.BKG_PNR_ID = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.BKG_CREDATETIME = null; } else {
      this.BKG_CREDATETIME = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.DFLOW_ID = null; } else {
      this.DFLOW_ID = Long.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.TRUEOD_ID = null; } else {
      this.TRUEOD_ID = Long.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.DENIED_BOARDING = null; } else {
      this.DENIED_BOARDING = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.NOSH_RATE = null; } else {
      this.NOSH_RATE = Double.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.INVOL_CHGDAT = null; } else {
      this.INVOL_CHGDAT = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.CXLDATETIME = null; } else {
      this.CXLDATETIME = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.DEMDATETIME = null; } else {
      this.DEMDATETIME = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.NB_PAX = null; } else {
      this.NB_PAX = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.UPDDATETIME = null; } else {
      this.UPDDATETIME = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.FLOW_LOWEST_CLS_IND = null; } else {
      this.FLOW_LOWEST_CLS_IND = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.RNG_LOWEST_CLS_IND = null; } else {
      this.RNG_LOWEST_CLS_IND = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.TKT_DATETIME = null; } else {
      this.TKT_DATETIME = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.GHOST_BKG = null; } else {
      this.GHOST_BKG = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.MASTER_IND = null; } else {
      this.MASTER_IND = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.FINALIZATION_DAT = null; } else {
      this.FINALIZATION_DAT = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.REG_NOSH = null; } else {
      this.REG_NOSH = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.REG_GOSH = null; } else {
      this.REG_GOSH = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.SLID_NOSH = null; } else {
      this.SLID_NOSH = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.SLID_GOSH = null; } else {
      this.SLID_GOSH = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.SHUP_RATE_UPDDATETIME = null; } else {
      this.SHUP_RATE_UPDDATETIME = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.CLASS_COMBI = null; } else {
      this.CLASS_COMBI = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.DEPDAT = null; } else {
      this.DEPDAT = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.POS = null; } else {
      this.POS = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.CXLRATE = null; } else {
      this.CXLRATE = Double.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.NB_NAMES = null; } else {
      this.NB_NAMES = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.PCT_NAMES = null; } else {
      this.PCT_NAMES = Double.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.SSCAP_CHECK_IND = null; } else {
      this.SSCAP_CHECK_IND = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.REG_SH = null; } else {
      this.REG_SH = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.SHUP_RATE = null; } else {
      this.SHUP_RATE = Double.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.IS_LOCAL = null; } else {
      this.IS_LOCAL = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.OUTBOUND_FLOW_DAT = null; } else {
      this.OUTBOUND_FLOW_DAT = java.sql.Timestamp.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.GRP_IND = null; } else {
      this.GRP_IND = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.BOARDED = null; } else {
      this.BOARDED = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.CAB_LVL_COMBI = null; } else {
      this.CAB_LVL_COMBI = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.POO = null; } else {
      this.POO = __cur_str;
    }

  }

  public Object clone() throws CloneNotSupportedException {
    YO_DFBS o = (YO_DFBS) super.clone();
    o.BKG_CREDATETIME = (o.BKG_CREDATETIME != null) ? (java.sql.Timestamp) o.BKG_CREDATETIME.clone() : null;
    o.INVOL_CHGDAT = (o.INVOL_CHGDAT != null) ? (java.sql.Timestamp) o.INVOL_CHGDAT.clone() : null;
    o.CXLDATETIME = (o.CXLDATETIME != null) ? (java.sql.Timestamp) o.CXLDATETIME.clone() : null;
    o.DEMDATETIME = (o.DEMDATETIME != null) ? (java.sql.Timestamp) o.DEMDATETIME.clone() : null;
    o.UPDDATETIME = (o.UPDDATETIME != null) ? (java.sql.Timestamp) o.UPDDATETIME.clone() : null;
    o.TKT_DATETIME = (o.TKT_DATETIME != null) ? (java.sql.Timestamp) o.TKT_DATETIME.clone() : null;
    o.FINALIZATION_DAT = (o.FINALIZATION_DAT != null) ? (java.sql.Timestamp) o.FINALIZATION_DAT.clone() : null;
    o.SHUP_RATE_UPDDATETIME = (o.SHUP_RATE_UPDDATETIME != null) ? (java.sql.Timestamp) o.SHUP_RATE_UPDDATETIME.clone() : null;
    o.DEPDAT = (o.DEPDAT != null) ? (java.sql.Timestamp) o.DEPDAT.clone() : null;
    o.OUTBOUND_FLOW_DAT = (o.OUTBOUND_FLOW_DAT != null) ? (java.sql.Timestamp) o.OUTBOUND_FLOW_DAT.clone() : null;
    return o;
  }

 
  private static long timeSpent = 0 ; 
 
   public Map<String, Object> getFieldMap() {
    Map<String, Object> __sqoop$field_map = new TreeMap<String, Object>();
    __sqoop$field_map.put("DFB_ID", this.DFB_ID);
    __sqoop$field_map.put("BKG_PNR_ID", this.BKG_PNR_ID);
    __sqoop$field_map.put("BKG_CREDATETIME", this.BKG_CREDATETIME);
    __sqoop$field_map.put("DFLOW_ID", this.DFLOW_ID);
    __sqoop$field_map.put("TRUEOD_ID", this.TRUEOD_ID);
    __sqoop$field_map.put("DENIED_BOARDING", this.DENIED_BOARDING);
    __sqoop$field_map.put("NOSH_RATE", this.NOSH_RATE);
    __sqoop$field_map.put("INVOL_CHGDAT", this.INVOL_CHGDAT);
    __sqoop$field_map.put("CXLDATETIME", this.CXLDATETIME);
    __sqoop$field_map.put("DEMDATETIME", this.DEMDATETIME);
    __sqoop$field_map.put("NB_PAX", this.NB_PAX);
    __sqoop$field_map.put("UPDDATETIME", this.UPDDATETIME);
    __sqoop$field_map.put("FLOW_LOWEST_CLS_IND", this.FLOW_LOWEST_CLS_IND);
    __sqoop$field_map.put("RNG_LOWEST_CLS_IND", this.RNG_LOWEST_CLS_IND);
    __sqoop$field_map.put("TKT_DATETIME", this.TKT_DATETIME);
    __sqoop$field_map.put("GHOST_BKG", this.GHOST_BKG);
    __sqoop$field_map.put("MASTER_IND", this.MASTER_IND);
    __sqoop$field_map.put("FINALIZATION_DAT", this.FINALIZATION_DAT);
    __sqoop$field_map.put("REG_NOSH", this.REG_NOSH);
    __sqoop$field_map.put("REG_GOSH", this.REG_GOSH);
    __sqoop$field_map.put("SLID_NOSH", this.SLID_NOSH);
    __sqoop$field_map.put("SLID_GOSH", this.SLID_GOSH);
    __sqoop$field_map.put("SHUP_RATE_UPDDATETIME", this.SHUP_RATE_UPDDATETIME);
    __sqoop$field_map.put("CLASS_COMBI", this.CLASS_COMBI);
    __sqoop$field_map.put("DEPDAT", this.DEPDAT);
    __sqoop$field_map.put("POS", this.POS);
    __sqoop$field_map.put("CXLRATE", this.CXLRATE);
    __sqoop$field_map.put("NB_NAMES", this.NB_NAMES);
    __sqoop$field_map.put("PCT_NAMES", this.PCT_NAMES);
    __sqoop$field_map.put("SSCAP_CHECK_IND", this.SSCAP_CHECK_IND);
    __sqoop$field_map.put("REG_SH", this.REG_SH);
    __sqoop$field_map.put("SHUP_RATE", this.SHUP_RATE);
    __sqoop$field_map.put("IS_LOCAL", this.IS_LOCAL);
    __sqoop$field_map.put("OUTBOUND_FLOW_DAT", this.OUTBOUND_FLOW_DAT);
    __sqoop$field_map.put("GRP_IND", this.GRP_IND);
    __sqoop$field_map.put("BOARDED", this.BOARDED);
    __sqoop$field_map.put("CAB_LVL_COMBI", this.CAB_LVL_COMBI);
    __sqoop$field_map.put("POO", this.POO);
    return __sqoop$field_map;
  }

  public void setField(String __fieldName, Object __fieldVal) {
    if ("DFB_ID".equals(__fieldName)) {
      this.DFB_ID = (Long) __fieldVal;
    }
    else    if ("BKG_PNR_ID".equals(__fieldName)) {
      this.BKG_PNR_ID = (String) __fieldVal;
    }
    else    if ("BKG_CREDATETIME".equals(__fieldName)) {
      this.BKG_CREDATETIME = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("DFLOW_ID".equals(__fieldName)) {
      this.DFLOW_ID = (Long) __fieldVal;
    }
    else    if ("TRUEOD_ID".equals(__fieldName)) {
      this.TRUEOD_ID = (Long) __fieldVal;
    }
    else    if ("DENIED_BOARDING".equals(__fieldName)) {
      this.DENIED_BOARDING = (Integer) __fieldVal;
    }
    else    if ("NOSH_RATE".equals(__fieldName)) {
      this.NOSH_RATE = (Double) __fieldVal;
    }
    else    if ("INVOL_CHGDAT".equals(__fieldName)) {
      this.INVOL_CHGDAT = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("CXLDATETIME".equals(__fieldName)) {
      this.CXLDATETIME = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("DEMDATETIME".equals(__fieldName)) {
      this.DEMDATETIME = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("NB_PAX".equals(__fieldName)) {
      this.NB_PAX = (Integer) __fieldVal;
    }
    else    if ("UPDDATETIME".equals(__fieldName)) {
      this.UPDDATETIME = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("FLOW_LOWEST_CLS_IND".equals(__fieldName)) {
      this.FLOW_LOWEST_CLS_IND = (Integer) __fieldVal;
    }
    else    if ("RNG_LOWEST_CLS_IND".equals(__fieldName)) {
      this.RNG_LOWEST_CLS_IND = (Integer) __fieldVal;
    }
    else    if ("TKT_DATETIME".equals(__fieldName)) {
      this.TKT_DATETIME = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("GHOST_BKG".equals(__fieldName)) {
      this.GHOST_BKG = (Integer) __fieldVal;
    }
    else    if ("MASTER_IND".equals(__fieldName)) {
      this.MASTER_IND = (Integer) __fieldVal;
    }
    else    if ("FINALIZATION_DAT".equals(__fieldName)) {
      this.FINALIZATION_DAT = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("REG_NOSH".equals(__fieldName)) {
      this.REG_NOSH = (Integer) __fieldVal;
    }
    else    if ("REG_GOSH".equals(__fieldName)) {
      this.REG_GOSH = (Integer) __fieldVal;
    }
    else    if ("SLID_NOSH".equals(__fieldName)) {
      this.SLID_NOSH = (Integer) __fieldVal;
    }
    else    if ("SLID_GOSH".equals(__fieldName)) {
      this.SLID_GOSH = (Integer) __fieldVal;
    }
    else    if ("SHUP_RATE_UPDDATETIME".equals(__fieldName)) {
      this.SHUP_RATE_UPDDATETIME = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("CLASS_COMBI".equals(__fieldName)) {
      this.CLASS_COMBI = (String) __fieldVal;
    }
    else    if ("DEPDAT".equals(__fieldName)) {
      this.DEPDAT = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("POS".equals(__fieldName)) {
      this.POS = (String) __fieldVal;
    }
    else    if ("CXLRATE".equals(__fieldName)) {
      this.CXLRATE = (Double) __fieldVal;
    }
    else    if ("NB_NAMES".equals(__fieldName)) {
      this.NB_NAMES = (Integer) __fieldVal;
    }
    else    if ("PCT_NAMES".equals(__fieldName)) {
      this.PCT_NAMES = (Double) __fieldVal;
    }
    else    if ("SSCAP_CHECK_IND".equals(__fieldName)) {
      this.SSCAP_CHECK_IND = (Integer) __fieldVal;
    }
    else    if ("REG_SH".equals(__fieldName)) {
      this.REG_SH = (Integer) __fieldVal;
    }
    else    if ("SHUP_RATE".equals(__fieldName)) {
      this.SHUP_RATE = (Double) __fieldVal;
    }
    else    if ("IS_LOCAL".equals(__fieldName)) {
      this.IS_LOCAL = (Integer) __fieldVal;
    }
    else    if ("OUTBOUND_FLOW_DAT".equals(__fieldName)) {
      this.OUTBOUND_FLOW_DAT = (java.sql.Timestamp) __fieldVal;
    }
    else    if ("GRP_IND".equals(__fieldName)) {
      this.GRP_IND = (Integer) __fieldVal;
    }
    else    if ("BOARDED".equals(__fieldName)) {
      this.BOARDED = (Integer) __fieldVal;
    }
    else    if ("CAB_LVL_COMBI".equals(__fieldName)) {
      this.CAB_LVL_COMBI = (String) __fieldVal;
    }
    else    if ("POO".equals(__fieldName)) {
      this.POO = (String) __fieldVal;
    }
    else {
      throw new RuntimeException("No such field: " + __fieldName);
    }
  }
}
