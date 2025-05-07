(ns repro
  (:require [afrolabs.components :as -comp]
            [afrolabs.config :as -config]
            [afrolabs.components.kafka :as -kafka]
            [afrolabs.components.confluent :as -confluent]
            [afrolabs.components.kafka.utilities :as -kafka-utils]
            [integrant.core :as ig]
            [nextjournal.clerk :as clerk]))

^{::clerk/visibility {:code :hide :result :hide}}
(comment
  (clerk/serve! {:host "0.0.0.0"
                 :watch-paths ["notebooks"]})
  (clerk/halt!)
  )

^{::clerk/visibility {:code :show :result :hide}}
(def cfg {:kafka-bootstrap-server "localhost:9092"
          :confluent-api-key      nil
          :confluent-api-secret   nil})
^{::clerk/visibility {:code :show :result :hide}}
(def topic-name "test-topic")

;; ## If this works...
;; ... then it's good
^{::clerk/visibility {:code :show :result :hide}}
(defn load-msgs []
  (let [msgs
        (let [_run-at #inst "2025-05-06T07:16:47.060791288Z"]
          (-kafka-utils/load-messages-from-confluent-topic :bootstrap-server (:kafka-bootstrap-server cfg)
                                                           :api-key           (:confluent-api-key cfg)
                                                           :api-secret        (:confluent-api-secret cfg)
                                                           :topics            [topic-name]
                                                           :extra-strategies  [(-kafka/EdnSerializer :consumer :both)
                                                                               (-kafka/OffsetReset "earliest")]
                                                           :collect-messages? true
                                                           :nr-msgs           1000))]
    msgs))

;; ## How to actually reproduce this.
;; An assumption is made about local kafka running on port 9092
^{::clerk/visibility {:code :show :result :hide}}
(comment
  (def component-base-cfg {:bootstrap-server     (:kafka-bootstrap-server cfg)
                           :topic-name-providers [(reify -kafka/ITopicNameProvider
                                                    (-kafka/get-topic-names [_]
                                                      [topic-name]))]
                           :strategies           [(-confluent/ConfluentCloud {:api-key    (:confluent-api-key cfg)
                                                                              :api-secret (:confluent-api-secret cfg)})]
                           })

  ;; asserts the topics as a side-effect of initialization
  (def _asserter (->> (assoc component-base-cfg :nr-of-partitions 2)
                      (ig/init-key :afrolabs.components.kafka/topic-asserter)
                      (ig/halt-key! :afrolabs.components.kafka/topic-asserter)))

  ;; we need a producer
  (def producer-cfg (-> component-base-cfg
                        (update :strategies conj (-kafka/EdnSerializer :producer :both))))
  (def producer (-kafka/make-producer producer-cfg))
  (-comp/halt producer)

  ;; Produce something to the topic
  (-kafka/produce! producer
                   [{:key   "key-1"
                     :value "value-1"
                     :topic topic-name}
                    {:topic topic-name
                     :key   "key-2"
                     :value {:more-complex [:options :are]
                             :available    {:it    "is"
                                            ::just "data after all"}}}])

  ;; just a reminder
  (-comp/halt producer)

  )

;; ## Kaboom ?!

(def msgs (load-msgs))
