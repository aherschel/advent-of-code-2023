
(def testinput ["1abc2"
                "pqr3stu8vwx"
                "a1b2c3d4e5f"
                "treb7uchet"])

(defn
  processrow
  [val]
  (let [n (re-seq #"\d+" val)
        first (first (first n))
        last (last (last n))]
    (Integer/parseInt (str first last))))

(defn
  process
  [vals] 
  (reduce + (map processrow vals)))

(defn
  read-file-as-list
  [file-path]
  (with-open [rdr (java.io.BufferedReader. (java.io.FileReader. file-path))] (doall (line-seq rdr))))

(comment 
  (processrow (testinput 0))
  (def realinput (read-file-as-list "1_1/input.txt"))
  (= (process testinput) 142)
  (process realinput)
  (processrow "fds149fds2"))
