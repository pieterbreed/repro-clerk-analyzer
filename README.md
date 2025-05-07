# Reproduction of a bug, maybe

## Environment

``` shell
$ cat .tool-versions
```

``` text
clojure 1.12.0.1530
java openjdk-24
```

## Prepping / Preparationing / Prepperrationalization

``` clojure
$ clj -X:deps prep
```

``` text
Downloading: org/clojure/tools.deps.cli/0.11.86/tools.deps.cli-0.11.86.pom from central
Downloading: org/clojure/tools.deps/0.22.1488/tools.deps-0.22.1488.pom from central
Downloading: org/clojure/tools.deps/0.22.1488/tools.deps-0.22.1488.jar from central
Downloading: org/clojure/tools.deps.cli/0.11.86/tools.deps.cli-0.11.86.jar from central
Prepping com.github.gometro/afrolabs-clj in /home/pieter/.gitlibs/libs/com.github.gometro/afrolabs-clj/a17c7c2e3ef01d6f00f548fc5291c269b4124b2a
```

## cider's repl command

``` shell
/home/pieter/.asdf/shims/clojure -Sdeps \{\:deps\ \{nrepl/nrepl\ \{\:mvn/version\ \"1.3.1\"\}\ cider/cider-nrepl\ \{\:mvn/version\ \"0.55.7\"\}\}\ \:aliases\ \{\:cider/nrepl\ \{\:main-opts\ \[\"-m\"\ \"nrepl.cmdline\"\ \"--middleware\"\ \"\[cider.nrepl/cider-middleware\]\"\]\}\}\} -M:repl:notebooks:cider/nrepl
```
