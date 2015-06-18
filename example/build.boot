(set-env!
 :resource-paths #{"resources"}
 :dependencies '[[org.martinklepsch/boot-gzip "0.1.1"]])

(require '[org.martinklepsch.boot-gzip :refer [gzip]])

(deftask compress []
  (gzip :files {"normalize.css" "n.css.gz"
                "reset.css"     "r.css.gz"}
        :regex [#"nested/*"]))
