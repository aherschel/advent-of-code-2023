(ns aoc.utils
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn read-file-as-list
  "Read in the given file path and return a list of strings"
  [file-path]
  (with-open [rdr (java.io.BufferedReader. (java.io.FileReader. file-path))]
    (doall (line-seq rdr))))

(defn split-string
  "Split a string into a list of chars"
  [hand]
  (str/split hand #""))
