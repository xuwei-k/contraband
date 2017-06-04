import Dependencies._

lazy val pluginSettings = Seq(
  bintrayPackage := "sbt-contraband",
  sbtPlugin := true
)

lazy val root = (project in file(".")).
  enablePlugins(NoPublish, TravisSitePlugin).
  aggregate(library, plugin).
  settings(
    inThisBuild(List(
      version := "0.3.0-SNAPSHOT",
      organization := "org.scala-sbt",
      crossScalaVersions := Seq("2.12.1", "2.11.8", "2.10.6"),
      scalaVersion := "2.10.6",
      organizationName := "sbt",
      organizationHomepage := Some(url("http://scala-sbt.org/")),
      homepage := Some(url("http://scala-sbt.org/contraband")),
      licenses += ("Apache-2.0", url("https://github.com/sbt/contraband/blob/master/LICENSE")),
      bintrayVcsUrl := Some("git@github.com:sbt/contraband.git"),
      scmInfo := Some(ScmInfo(url("https://github.com/sbt/contraband"), "git@github.com:sbt/contraband.git")),
      developers := List(
        Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n")),
        Developer("dwijnand", "Dale Wijnand", "@dwijnand", url("https://github.com/dwijnand")),
        Developer("Duhemm", "Martin Duhem", "@Duhemm", url("https://github.com/Duhemm"))
      ),
      description := "Contraband is a description language for your datatypes and APIs, currently targeting Java and Scala."
    )),
    name := "contraband root",
    siteGithubRepo := "sbt/contraband",
    siteEmail := { "eed3si9n" + "@" + "gmail.com" }
  )

lazy val library = (project in file("library")).
  enablePlugins(KeywordPlugin, SonatypePublish).
  disablePlugins(BintrayPlugin).
  settings(
    name := "contraband",
    libraryDependencies ++= Seq(parboiled) ++ jsonDependencies ++ Seq(scalaTest % Test, diffutils % Test)
  )

lazy val plugin = (project in file("plugin")).
  enablePlugins(BintrayPublish).
  settings(
    // crossBuildingSettings,
    pluginSettings,
    name := "sbt-contraband",
    description := "sbt plugin to generate growable datatypes.",
    ScriptedPlugin.scriptedSettings,
    ScriptedPlugin.scriptedRun := {
      if((sbtVersion in pluginCrossBuild).value == "1.0.0-M5") {
        // https://github.com/sbt/sbt/blob/v1.0.0-M6/scripted/plugin/src/main/scala/sbt/ScriptedPlugin.scala#L73-L81
        // workaround https://github.com/sbt/sbt/issues/3245
        ScriptedPlugin.scriptedTests.value.getClass.getMethod("run",
                                             classOf[File],
                                             classOf[Boolean],
                                             classOf[Array[String]],
                                             classOf[File],
                                             classOf[Array[String]],
                                             classOf[java.util.List[File]])
      } else {
        ScriptedPlugin.scriptedRunTask.value
      }
    },
    // https://github.com/sbt/sbt/blob/v1.0.0-M6/scripted/plugin/src/main/scala/sbt/ScriptedPlugin.scala#L130-L144
    ScriptedPlugin.scripted := {
      if((sbtVersion in pluginCrossBuild).value == "1.0.0-M5") {
        val p = ScriptedPlugin.asInstanceOf[{def scriptedParser(f: File): complete.Parser[Seq[String]]}]
        Def.inputTask {
          val args = p.scriptedParser(sbtTestDirectory.value).parsed
          val prereq: Unit = scriptedDependencies.value
          try {
            scriptedRun.value.invoke(
              scriptedTests.value,
              sbtTestDirectory.value,
              scriptedBufferLog.value: java.lang.Boolean,
              args.toArray,
              sbtLauncher.value,
              scriptedLaunchOpts.value.toArray,
              new java.util.ArrayList()
            )
          } catch { case e: java.lang.reflect.InvocationTargetException => throw e.getCause }
        }
      } else {
        ScriptedPlugin.scriptedTask.evaluated
      }
    },
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
    },
    publishLocal := (publishLocal dependsOn (publishLocal in library)).value
  ).
  dependsOn(library)
