let
   pkgs = import <nixpkgs> {};
in pkgs.stdenv.mkDerivation rec {
  name = "nash-env";
  buildInputs = [ pkgs.freetype ];
  LD_LIBRARY_PATH = with pkgs.xlibs; "${pkgs.mesa}/lib:${libXt}/lib:${pkgs.libxkbcommon}/lib:${libX11}/lib";
}

# :${libXcursor}/lib:${libXxf86vm}/lib:${libXi}/lib
