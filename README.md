# boot-gzip

Boot task to `gzip` files and nothing else.

[](dependency)
```clojure
[org.martinklepsch/boot-gzip "0.1.0"] ;; latest release
```
[](/dependency)

## Usage

**In a shell**

In a terminal you can compile any `defstyles`-defined stylesheet as follows:

```
;; you can use --files or just -f
boot gzip --files normalize.css:n.css.gz -f reset.css:r.css.gz
```

**In your `build.boot`**

```
(deftask compress []
  (gzip :files {"normalize.css" "n.css.gz"
                "reset.css"     "r.css.gz"}))
```

**You can try this in the example directory of this project

## Options

See the [boot project](https://github.com/boot-clj/boot) for more information
on how to use these.

```clojure
[f files ORIGIN:TARGET   {str str}   "{origin target} map of files to gzip"]
```

## License

Copyright Martin Klepsch 2014.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
