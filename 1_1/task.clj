(defn read-file-as-list
  [file-path]
  (with-open [rdr (java.io.BufferedReader. (java.io.FileReader. file-path))] 
    (doall (line-seq rdr))))

(def test-input
  '("1abc2"
    "pqr3stu8vwx"
    "a1b2c3d4e5f"
    "treb7uchet"))

(def input (read-file-as-list "1_1/input.txt"))

(defn process-row
  [val]
  (let [n (re-seq #"\d+" val)]
    (Integer/parseInt (str (first (first n)) (last (last n))))))

(defn process
  [vals] 
  (reduce + (map process-row vals)))

(comment 
  (process-row (test-input 0))
  (= (process test-input) 142)
  (process input)
  (process-row "fds149fds2"))
