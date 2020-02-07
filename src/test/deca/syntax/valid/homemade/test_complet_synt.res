`> [1, 0] Program
   +> ListDeclClass [List with 1 elements]
   |  []> [1, 0] DeclClass
   |      +> [1, 6] Identifier (A)
   |      +> [builtin] Identifier (Object)
   |      `> ListDeclField [List with 1 elements]
   |         []> [2, 17] DeclField
   |             +> PROTECTED
   |             +> [2, 13] Identifier (int)
   |             +> [2, 17] Identifier (x)
   |             `> NoInitialization
   |      +> ListDeclMethod [List with 2 elements]
   |      |  []> [3, 3] DeclMethod
   |      |  ||  +> [3, 3] Identifier (int)
   |      |  ||  +> [3, 7] Identifier (getX)
   |      |  ||  +> ListDeclParam [List with 0 elements]
   |      |  ||  `> [3, 14] DecaMethodBody
   |      |  ||     +> ListDeclVar [List with 0 elements]
   |      |  ||     `> ListInst [List with 1 elements]
   |      |  ||        []> [4, 6] Return
   |      |  ||            `> [4, 13] Identifier (x)
   |      |  []> [6, 3] DeclMethod
   |      |      +> [6, 3] Identifier (void)
   |      |      +> [6, 8] Identifier (setX)
   |      |      +> ListDeclParam [List with 1 elements]
   |      |      |  []> [6, 13] DeclParam
   |      |      |      +> [6, 13] Identifier (int)
   |      |      |      `> [6, 17] Identifier (x)
   |      |      `> [6, 20] DecaMethodBody
   |      |         +> ListDeclVar [List with 0 elements]
   |      |         `> ListInst [List with 1 elements]
   |      |            []> [7, 13] Assign
   |      |                +> [7, 10] FieldSelection
   |      |                |  +> [7, 6] This
   |      |                |  `> [7, 11] Identifier (x)
   |      |                `> [7, 15] Identifier (x)
   `> [11, 0] Main
      +> ListDeclVar [List with 1 elements]
      |  []> [12, 5] DeclVar
      |      +> [12, 3] Identifier (A)
      |      +> [12, 5] Identifier (a)
      |      `> [12, 7] Initialization
      |         `> [12, 9] New
      |            `> [12, 13] Identifier (A)
      `> ListInst [List with 2 elements]
         []> [13, 9] MethodCall
         ||  +> [13, 3] Identifier (a)
         ||  +> [13, 5] Identifier (setX)
         ||  `> ListExpr [List with 1 elements]
         ||     []> [13, 10] Int (1)
         []> [14, 3] Println
             `> ListExpr [List with 2 elements]
                []> [14, 11] StringLiteral (a.getX() = )
                []> [14, 32] MethodCall
                    +> [14, 26] Identifier (a)
                    +> [14, 28] Identifier (getX)
                    `> ListExpr [List with 0 elements]
