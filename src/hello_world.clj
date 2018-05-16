(ns hello-world
  (:gen-class
    :methods [^:static [foo [java.util.Map] com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent]])
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.string :as string])
  (:import [com.amazonaws.services.lambda.runtime.events APIGatewayProxyResponseEvent]))

(defn ^:private key->keyword [key-string]
  (-> key-string
      (string/replace #"([a-z])([A-Z])" "$1-$2")
      (string/replace #"([A-Z]+)([A-Z])" "$1-$2")
      string/lower-case
      keyword))

(defn ^:private in->event [in]
  (with-open [reader (io/reader in)]
    (json/read reader :key-fn key->keyword)))

(defn ^:private ->response [{:keys [status body]}]
  (doto (APIGatewayProxyResponseEvent.)
    (.setStatusCode (int status))
    (.setBody body)))

(defn -foo [event]
  (println "others")
  (pp/pprint event)
  (->response
    {:status 200
     :body "FOO NUTES"}))
