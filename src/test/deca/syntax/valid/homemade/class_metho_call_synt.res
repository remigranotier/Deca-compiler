`> [9, 0] Program
   +> ListDeclClass [List with 1 elements]
   |  []> [9, 0] DeclClass
   |      +> [9, 6] Identifier (Ouaip)
   |      +> [9, 20] Identifier (ouaipp)
   |      `> ListDeclField [List with 0 elements]
   |      +> ListDeclMethod [List with 2 elements]
   |      |  []> [10, 4] DeclMethod
   |      |  ||  +> [10, 4] Identifier (void)
   |      |  ||  +> [10, 9] Identifier (gigaOuaip)
   |      |  ||  +> ListDeclParam [List with 0 elements]
   |      |  ||  `> [10, 21] DecaMethodBody
   |      |  ||     +> ListDeclVar [List with 0 elements]
   |      |  ||     `> ListInst [List with 1 elements]
   |      |  ||        []> [11, 8] Print
   |      |  ||            `> ListExpr [List with 1 elements]
   |      |  ||               []> [11, 14] Identifier (ouais)
   |      |  []> [14, 4] DeclMethod
   |      |      +> [14, 4] Identifier (int)
   |      |      +> [14, 8] Identifier (wesh)
   |      |      +> ListDeclParam [List with 1 elements]
   |      |      |  []> [14, 13] DeclParam
   |      |      |      +> [14, 13] Identifier (int)
   |      |      |      `> [14, 17] Identifier (weshalors)
   |      |      `> [14, 28] DecaMethodBody
   |      |         +> ListDeclVar [List with 0 elements]
   |      |         `> ListInst [List with 1 elements]
   |      |            []> [15, 8] Return
   |      |                `> [15, 25] Plus
   |      |                   +> [15, 15] Identifier (weshalors)
   |      |                   `> [15, 27] Int (1)
   `> [19, 0] Main
      +> ListDeclVar [List with 1 elements]
      |  []> [20, 10] DeclVar
      |      +> [20, 4] Identifier (Ouaip)
      |      +> [20, 10] Identifier (oui)
      |      `> [20, 14] Initialization
      |         `> [20, 16] New
      |            `> [20, 20] Identifier (Ouaip)
      `> ListInst [List with 2 elements]
         []> [21, 12] MethodCall
         ||  +> [21, 4] Identifier (oui)
         ||  +> [21, 8] Identifier (wesh)
         ||  `> ListExpr [List with 1 elements]
         ||     []> [21, 13] Int (3)
         []> [22, 4] Println
             `> ListExpr [List with 1 elements]
                []> [22, 12] StringLiteral (nice)
