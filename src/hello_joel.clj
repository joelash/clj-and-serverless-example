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
  (assert (= (count args) 3) "lambda function must have exactly three args")
  (let [prefix (gensym)
        handleRequestMethod (symbol (str prefix "handleRequest"))]
    `(do
       (gen-class
        :name ~name
        :prefix ~prefix
        :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
       (defn ~handleRequestMethod
         ~(into ['this] args)
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

(defapigateway hello-joel.foo2 [in out context]
  (let [event (in->event in)]
    (pp/pprint event)
    (with-open [writer (ObjectOutputStream. out)]
      (.writeObject writer
                    (->response {:status 200
                                 :body {:hello :world2}})))))
