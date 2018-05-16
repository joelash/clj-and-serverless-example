(defproject hello-world "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [com.amazonaws/aws-lambda-java-core "1.2.0"]
                 [com.amazonaws/aws-lambda-java-events "2.1.0"]]
  ;; :java-source-paths ["src/java"]
  :aot :all)
