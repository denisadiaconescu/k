// Copyright (c) 2018 K Team. All Rights Reserved.

requires "a.k"

module A-SPEC

  imports A

  rule <k> call(X) => X ... </k>
       <calldata> _ => _ </calldata>
       <flag> 0 => 1 </flag>

  rule <k> run => X ... </k>
       <calldata> bytes2(nth(X,0,2), nth(X,1,2)) </calldata>
       <flag> 0 => 1 </flag>
       [trusted]

endmodule
