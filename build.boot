(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure       "1.6.0"  :scope "provided"]
                  [adzerk/bootlaces          "0.1.11" :scope "test"]])

(require '[adzerk.bootlaces :refer [bootlaces! build-jar push-release]])

(def +version+ "0.1.0")

(bootlaces! +version+)

(task-options!
 pom  {:project     'org.martinklepsch/boot-gzip
       :version     +version+
       :description "Boot task to gzip files"
       :url         "https://github.com/martinklepsch/boot-gzip"
       :scm         {:url "https://github.com/martinklepsch/boot-gzip"}
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})
