ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.12.15"
//resolvers += Resolver.mavenLocal
externalResolvers := List("aliyun" at "https://maven.aliyun.com/repository/public")

lazy val root = (project in file("."))
  .settings(
    name := "spark"
  )
// 引入 Spark 依赖项
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.3.2",
  "org.apache.spark" %% "spark-sql" % "3.3.2",
  "org.apache.spark" %% "spark-streaming" % "3.3.2",
  "org.apache.spark" %% "spark-hive" % "3.3.2",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.3.2",
  "com.mysql" % "mysql-connector-j" % "8.0.33"
  // 添加其他 Spark 相关依赖项...
)
