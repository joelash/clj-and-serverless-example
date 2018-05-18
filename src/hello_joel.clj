(ns hello-joel
  (:require
    [camel-snake-kebab.core :as csk]
    [camel-snake-kebab.extras :as csk.extras]
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.string :as string]))

;; Adapted from https://github.com/uswitch/lambada
(defmacro defhandler-http
  "Create a named class that can be invoked as a AWS Lambda function."
  [name args & body]
  (assert (= 1 (count args)) "Invalid arity")
  (let [this-ns (ns-name *ns*)
        class-name (symbol (str this-ns "." name))
        fn-name (symbol (str "-" name))
        event-name (symbol (first args))]
    `(do
       (gen-class
        :name ~class-name
        :methods [^:static [~name [java.util.Map] java.util.Map]])
       (defn ~fn-name [~event-name]
         (let [~event-name (->> ~event-name (into {}) (csk.extras/transform-keys csk/->kebab-case-keyword))
               resp# (do ~@body)]
           (assert (:status resp#) "Must return a status")
           (cond-> {"statusCode" (int (:status resp#))}
             (:body resp#) (assoc "body" (json/write-str (:body resp#)))
             (:headers resp#) (assoc "headers"
                                     (->> resp#
                                          :headers
                                          (csk.extras/transform-keys csk/->HTTP-Header-Case-String)))
             :always (java.util.HashMap.)))))))

(defhandler-http foo [event]
  (pp/pprint event)
  {:status 200
   :body {:hello :world-joel-3}
   :headers {:x-foo-bar "125"}})
