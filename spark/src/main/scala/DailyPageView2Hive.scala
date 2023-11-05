import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession, functions}
import org.apache.spark.sql.types.{IntegerType, LongType, StringType, StructField, StructType}

import java.io.FileInputStream
import java.sql.{Connection, DriverManager}
import java.util.Properties

object DailyPageView2Hive {

  private var url : String = ""

  private val properties = new Properties()

  loadProperties()

  private def loadProperties(): Properties = {
    val path = Thread.currentThread().getContextClassLoader.getResource("db.properties").getPath
    properties.load(new FileInputStream(path))
    url = properties.get("url").toString
    properties
  }

  def main(args: Array[String]): Unit = {
    //spark设置
    System.setProperty("HADOOP_USER_NAME", "root")
    val conf = new SparkConf().setAppName("DailyPageView2Hive").setMaster("local[8]")
    val spark = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    //获取状态为已解压（2）的文件记录列表
    val fileRecords = getFilesFromMySQL(spark)
    val fileList: List[Row] = fileRecords.collect().toList
    val filePaths: Array[String] = fileList.map(row => row.getString(1)).toArray
    if (filePaths.isEmpty) {
      return
    }
    //创建wiki.page_view_orc表
    val sql = "CREATE TABLE IF NOT EXISTS wiki.page_view_orc (" +
      "domain_code STRING," +
      "page_title STRING," +
      "count_views INT," +
      "total_response_size BIGINT," +
      "hour STRING) " +
      "USING orc " +
      "partitioned by (year int, month int, day int)"
    spark.sql(sql)
    //csv中使用空格分隔各列数据
    val df: DataFrame = spark.read.format("csv").schema(pageViewSchema).option("sep", " ").load(filePaths.toSeq:_*)
    //通过内置函数获取当前正在读取的文件名，并放入path这一列
    val df_path = df.withColumn("path", functions.input_file_name())
    //从path列的文件名中提取出用于分区的day和表中的hour列数据，然后移除path列
    val df_day = df_path
      .withColumn("hour", functions.split(df_path.col("path"), "-")(3))
      .withColumn("day", functions.split(df_path.col("path"), "-")(2).cast("Int"))
      .drop("path")
    //从day列的文件名中提取出用于分区的month和year列数据
    val df_processed = df_day
      .withColumn("month", functions.substring(df_day.col("day"), 5, 2).cast("Int"))
      .withColumn("year", functions.substring(df_day.col("day"), 0, 4).cast("Int"))
    df_processed.show(10)
    df_processed.write.partitionBy("year", "month", "day").mode(SaveMode.Append).format("orc")
      .saveAsTable("wiki.page_view_orc")
    //更新文件记录状态
    updateFileRecords(fileList)
  }

  private def getFilesFromMySQL(sparkSession: SparkSession): DataFrame = {
    val df: DataFrame = sparkSession.read.jdbc(url, "file_process_record", properties)
      .select("file_name", "path", "status")
      .where("status = 2")
    df.show(10)
    df
  }

  private def pageViewSchema: StructType = {
    StructType(
      Array(
        StructField("domain_code", StringType),
        StructField("page_title", StringType),
        StructField("count_views", IntegerType),
        StructField("total_response_size", LongType)
      )
    )
  }

  private def updateFileRecords(list : List[Row]): Unit = {
    val connection: Connection = DriverManager.getConnection(url, properties)
    val statement = connection.prepareStatement("UPDATE file_process_record set `status`=99 WHERE `file_name`=?")
    list.foreach(row => {
      val fileName = row.getAs[String]("file_name")
      statement.setString(1, fileName)
      statement.addBatch()
    })
    //执行语句
    statement.executeBatch()
    //释放资源
    statement.close()
    connection.close()
  }


}

