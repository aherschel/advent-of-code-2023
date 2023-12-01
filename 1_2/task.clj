(require '[clojure.string :as str])

(def testinput '("two1nine"
                "eightwothree"
                "abcone2threexyz"
                "xtwone3four"
                "4nineeightseven2"
                "zoneight234"
                "7pqrstsixteen"))

(defn pre-pre-process-row
  [val] 
  (vec (re-seq #"zero|one|two|three|four|five|six|seven|eight|nine|0|1|2|3|4|5|6|7|8|9" val)))

;; This approach doesn't work, because shared characters seem
;; to count towards BOTH numbers, there's no numeric priority
(defn
  preprocessrow
  [val]
  (-> val
      (str/replace "one" "1")
      (str/replace "two" "2")
      (str/replace "three" "3")
      (str/replace "four" "4")
      (str/replace "five" "5")
      (str/replace "six" "6")
      (str/replace "seven" "7")
      (str/replace "eight" "8")
      (str/replace "nine" "9")))

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
  (reduce + (map processrow (map preprocessrow (map str/join (map pre-pre-process-row vals))))))

(defn
  read-file-as-list
  [file-path]
  (with-open [rdr (java.io.BufferedReader. (java.io.FileReader. file-path))] (doall (line-seq rdr))))

(comment
  (processrow (testinput 0))
  (def realinput (read-file-as-list "1_2/input.txt"))
  (= (process testinput) 281)
  (process realinput)
  (preprocessrow (str/join (pre-pre-process-row "2fiveshtds4oneightsjg")))
  (processrow (preprocessrow (str/join (pre-pre-process-row "2fiveshtds4oneightsjg"))))
  (= (processrow "2fiveshtds4oneightsjg") 28)
  (map preprocessrow (re-seq #"one|two|three|four|five|six|seven|eight|nine|0|1|2|3|4|5|6|7|8|9" "2fiveshtds4oneightsjg"))
  (->> testinput pre-pre-process-row preprocessrow)
  (reduce + (map processrow (map preprocessrow (map str/join (map pre-pre-process-row testinput)))))
  (reduce + (map processrow (map preprocessrow (map str/join (map pre-pre-process-row (read-file-as-list "1_2/input.txt")))))))

