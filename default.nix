{ pkgs ? import <nixpkgs> {}, ... }:

let
  jdk = pkgs.adoptopenjdk-hotspot-bin-11;
  sbt = pkgs.sbt.override { jre = jdk; };
in
  pkgs.stdenv.mkDerivation {
    name = "gcs-storage-stream";
    src = ./.;
    buildInputs = [ jdk sbt ];
  }
