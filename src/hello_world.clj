(ns hello-world
  (:gen-class
    :methods [^:static [foo [java.util.Map] com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent]])
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.string :as string])
  (:import [com.amazonaws.services.lambda.runtime.events APIGatewayProxyResponseEvent]))

(defn ^:private ->response [{:keys [status body]}]
  (doto (APIGatewayProxyResponseEvent.)
    (.setStatusCode (int status))
    (.setBody body)))

(defn -foo2 [event]
  (println "others")
  (pp/pprint event)
  (->response
    {:status 200
     :body "FOO NUTES"}))
