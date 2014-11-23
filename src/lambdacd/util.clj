(ns lambdacd.util
  (:import (java.nio.file Files)
           (java.nio.file.attribute FileAttribute))
  (:require [clojure.core.async :as async]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.java.shell :as jsh]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]))

(defn range-from [from len] (range (inc from) (+ (inc from) len)))

(defn is-channel? [c]
  (satisfies? clojure.core.async.impl.protocols/Channel c))

(defn create-temp-dir []
  (str (Files/createTempDirectory "foo" (into-array FileAttribute []))))

(defn write-as-json [file data]
  (spit file (json/write-str data)))

(defn bash
  [cwd & commands]
  (let [combined-command (str "bash -c '" (string/join " && " commands) "' 2>&1") ;; very hacky but it does the job of redirecting stderr to stdout
        result (jsh/sh "bash" "-c" combined-command  :dir cwd)]
    (log/debug (str "executed " combined-command " in " cwd " with result " result))
    result))


(defn map-if [pred f coll]
  "applies f to all elements in coll where pred is true"
  (map #(if (pred %)
         (f %)
         %) coll))