(ns aoc.day10
  (:require [aoc.utils :as utils]))

(def test-input (utils/read-file-as-list "resources/day10_test.txt"))
(def real-input (utils/read-file-as-list "resources/day10.txt"))

(defn to-grid
  "Given a list of strings, return a 2d array of the map"
  [vals]
  (to-array-2d (map utils/split-string vals)))

(defn empty-boolean-grid
  "Create an empty 2d array of given size, set to false"
  [size]
  (to-array-2d (map #(apply list %) (make-array Boolean/TYPE size size))))

(defn get-at
  "Given a position object, get the item at that position"
  [grid {:keys [x y]}]
  (aget grid y x))

(defn get-starting-pos
  "Find the [x y] coords of the starting position 'S'"
  [grid]
  (loop [pos {:x 0 :y 0}]
    (cond
      (= (get-at grid pos) "S") pos
      (= (:x pos) (dec (alength grid))) (recur {:x 0 :y (inc (:y pos))})
      :else (recur {:x (inc (:x pos)) :y (:y pos)}))))

(def spaces {"|" '(:up :down)
             "-" '(:left :right)
             "L" '(:up :right)
             "J" '(:up :left)
             "7" '(:down :left)
             "F" '(:down :right)
             "." '()
             "S" '(:up :down :left :right)})

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defn get-relative-position
  "Given a grid, position, and direction, return either a position map or null
   TODO: Check the character to see if it can receive this direction"
  [grid {:keys [x y]} direction]
  (case direction
    :up (if (and
             (not= 0 y)
             (in? (get spaces (get-at grid {:x x :y (dec y)})) :down))
          {:x x :y (dec y)}, nil)
    :down (if (and
               (not= (dec (alength grid)) y) 
               (in? (get spaces (get-at grid {:x x :y (inc y)})) :up)) 
            {:x x :y (inc y)}, nil)
    :left (if (and
               (not= 0 x)
               (in? (get spaces (get-at grid {:x (dec x) :y y})) :right))
             {:x (dec x) :y y}, nil)
    :right (if (and
                (not= (dec (alength grid)) x)
                (in? (get spaces (get-at grid {:x (inc x) :y y})) :left))
             {:x (inc x) :y y}, nil)))

(defn unvisited?
  "Return true if a position has been visited"
  [visited pos]
  (not (get-at visited pos)))

(defn get-moves
  "Get a list of moves from a grid and given position,
   return next-available-moves as a list"
  [grid visited pos]
  (filter
   (partial unvisited? visited)
   (filter some?
           (map 
            (partial get-relative-position grid pos) 
            (get spaces (get-at grid pos))))))

(defn visit!
  "Mutate the visited grid to indicate this has been visited"
  [visited {:keys [x y]}]
  (aset visited y x true))

(defn traverse-grid
  "We must recurse to traverse"
  [grid visited steps pos]
  (println (str "Visiting " pos " with " steps " steps"))
  (visit! visited pos)
  (let [moves (get-moves grid visited pos)
        next-traversal (partial traverse-grid grid visited (inc steps))]
    (if (empty? moves)
      steps
      (apply max (map next-traversal moves)))))

(defn part1
  "Solve problem part 1"
  [vals]
  (let [grid (to-grid vals)
        start (get-starting-pos grid)
        visited (empty-boolean-grid (alength grid))]
    (/ (inc (traverse-grid grid visited 0 start)) 2)))

;; Part 1 Tests
(comment
  (part1 test-input)
  (part1 (utils/read-file-as-list "resources/day10_test2.txt"))
  (= (part1 test-input) 8)
  (part1 real-input))

;; Part 1 second attempt

(defn get-next-move
  "Return a next move or nil, throws on multiple next steps"
  [grid visited pos]
  (let [moves (get-moves grid visited pos)]
    (case
     (empty? moves) nil
      (= 1 (count moves)) (first moves)
      :else (throw (Exception. "Encountered > 1 next moves, which is unexpected")))))

(comment
  (let [grid (to-grid test-input)
        visited (empty-boolean-grid (alength grid))
        pos {:x }]
    ))

(defn visit!
  "Mutate the visited grid to indicate this has been visited"
  [visited {:keys [x y]}]
  (aset visited y x true))

(defn traverse-grid
  "We must recurse to traverse"
  [grid visited steps pos]
  (println (str "Visiting " pos " with " steps " steps"))
  (visit! visited pos)
  (let [moves (get-moves grid visited pos)
        next-traversal (partial traverse-grid grid visited (inc steps))]
    (if (empty? moves)
      steps
      (apply max (map next-traversal moves)))))

(defn part1
  "Solve problem part 1"
  [vals]
  (let [grid (to-grid vals)
        start (get-starting-pos grid)
        visited (empty-boolean-grid (alength grid))]
    (/ (inc (traverse-grid grid visited 0 start)) 2)))

(defn part2
  "Solve problem part 2"
  [input]
  0)

;; Part 2 Tests
(comment
  (part2 test-input)
  (= (part2 test-input) 0)
  (part2 real-input))
