(ns hello-joel
  (:gen-class
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.string :as string]))

(defn ^:private key->keyword [key-string]
  (-> key-string
      (string/replace #"([a-z])([A-Z])" "$1-$2")
      (string/replace #"([A-Z]+)([A-Z])" "$1-$2")
      string/lower-case
      keyword))

(defn ^:private in->event [in]
  (with-open [reader (io/reader in)]
    (json/read reader :key-fn key->keyword)))

(defn -handleRequest [this in out context & [others]]
  (let [event (in->event in)]
    (pp/pprint event)
    (println "context")
    (pp/pprint context)
    (println "others")
    (pp/pprint others)
    (with-open [writer (io/writer out)]
      (json/write {:status 200
                   :body {:hello :world2}} writer)
      (.flush writer))))
