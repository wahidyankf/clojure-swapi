(ns clojure-swapi.rest-test
  (:require [clojure.data.json :as json]
            [clojure.string :as string])
  (:require [clj-http.client :as client]))

(defn get-artist-movies [artist-id]
  (let [artist (-> (str "https://swapi.dev/api/people/" artist-id)
                   client/get
                   :body
                   json/read-str)
        movies  (doall
                 (->> (get artist "films")
                      (map #(future (-> % client/get)))
                      (map deref)
                      (map (fn [res]
                             (->> res
                                  :body
                                  json/read-str)))
                      (map #(get % "title"))))]
    (str (get artist "name") " was featured in: " (string/join ", " movies) ".")))
(get-artist-movies 1)
;; => "Luke Skywalker was featured in: A New Hope, The Empire Strikes Back, Return of the Jedi, Revenge of the Sith."
(get-artist-movies 20)
;; => "Yoda was featured in: The Empire Strikes Back, Return of the Jedi, The Phantom Menace, Attack of the Clones, Revenge of the Sith."

