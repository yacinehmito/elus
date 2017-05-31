with (import <nixpkgs> {});
stdenv.mkDerivation {
  name = "elus";
  buildInputs = [
    openjdk8
    gradle
    jetbrains.idea-ultimate
  ];
}
