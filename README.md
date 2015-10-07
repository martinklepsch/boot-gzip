# boot-gzip

Boot task to `gzip` files and nothing else.

[](dependency)
```clojure
[org.martinklepsch/boot-gzip "0.1.2"] ;; latest release
```
[](/dependency)

## Usage

**In a shell**

In a terminal you can gzip any files without having a Boot based project present like this:

```
;; you can use --files or just -f
boot --resource-paths "resources" \
     -d org.martinklepsch/boot-gzip gzip \
     gzip --files normalize.css:n.css.gz -f reset.css:r.css.gz
```

**In your `build.boot`**

```
(require '[org.martinklepsch.boot-gzip :refer [gzip]])

(deftask compress []
  (gzip :files {"normalize.css" "n.css.gz"
                "reset.css"     "r.css.gz"}
        ;; Also you can compress files matching regular expressions:
        :regex [#"nested/*"]))
```

The tasks output will look show original and new filesizes as well savings in percent.
```
Gzipped nested/norm.css (7.6 KiB) to nested/norm.css.gz (2.5 KiB), saving 67%
Gzipped normalize.css (7.6 KiB) to n.css.gz (2.5 KiB), saving 67%
Gzipped reset.css (1.1 KiB) to r.css.gz (615 B), saving 44%
```
**You can try this in the `example/` directory of this project**

## Options

See the [boot project](https://github.com/boot-clj/boot) for more information
on how to use these.

```clojure
[r regex REGEX         [regex]   "Gzip all matching files to their respective location and append .gz"
 f files ORIGIN:TARGET {str str} "{origin target} map of files to gzip"]
```

## License

Copyright Martin Klepsch 2014.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
