(ns hello-joel
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.string :as string])
  (:import
    [com.amazonaws.services.lambda.runtime.events APIGatewayProxyResponseEvent]
    [java.io ObjectOutputStream]))

;; https://github.com/uswitch/lambada/blob/master/src/uswitch/lambada/core.clj
(defmacro defapigateway
  "Create a named class that can be invoked as a AWS Lambda function."
  [name args & body]
  ;; (assert (= (count args) 3) "lambda function must have exactly three args")
  (let [this-ns (ns-name *ns*)
        class-name (symbol (str this-ns "." name))
        fn-name (symbol (str "-" name))]
    `(do
       (gen-class
        :name ~class-name
        :methods [^:static [~name [java.util.Map] com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent]])
       (defn ~fn-name
         ~args
         ~@body))))

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
    (.setBody (json/write-str body))))

(defapigateway foo2 [event]
  (pp/pprint event)
  (->response {:status 200
               :body {:hello :world2}}))
