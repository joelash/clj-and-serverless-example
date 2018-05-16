(ns hello-joel
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.string :as string])
  (:import
    [com.amazonaws.services.lambda.runtime.events APIGatewayProxyResponseEvent]))

;; Adapted from https://github.com/uswitch/lambada
(defmacro defapigateway
  "Create a named class that can be invoked as a AWS Lambda function."
  [name args & body]
  (let [this-ns (ns-name *ns*)
        class-name (symbol (str this-ns "." name))
        fn-name (symbol (str "-" name))]
    `(do
       (gen-class
        :name ~class-name
        :methods [^:static [~name [java.util.Map] com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent]])
       (defn ~fn-name
         ~args
         (let [resp# (do ~@body)]
           ;; TODO test setHeaders and how do i do a when or cond->
           (doto (APIGatewayProxyResponseEvent.)
             (.setStatusCode (int (:status resp#)))
             (.setBody (json/write-str (:body resp#)))
             #_(.setHeaders (json/write-str (:headers resp#)))))))))

(defapigateway foo [event]
  (pp/pprint event)
  {:status 200
   :body {:hello :world3}})
