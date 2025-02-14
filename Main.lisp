(defun main-loop ()
  (loop
    (format t "Menú~%")
    (format t "1. Calcular Fibonacci~%")
    (format t "2. Calcular Factorial~%")
    (format t "3. Convertir Fahrenheit a Celsius~%")
    (format t "4. Salir~%")
    (format t "Elige una opción: ")
    (let ((opcion (read)))
      (cond
        ((= opcion 1)
         (format t "Ingrese el valor de n para Fibonacci: ")
         (let ((n (read)))
           (format t "Fibonacci(~a) = ~a~%" n (fibonacci n))))
        
        ((= opcion 2)
         (format t "Ingrese el valor de n para Factorial: ")
         (let ((n (read)))
           (format t "Factorial(~a) = ~a~%" n (factorial n))))
        
        ((= opcion 3)
         (format t "Ingrese la temperatura en Fahrenheit: ")
         (let ((f (read)))
           (format t "~a°F = ~a°C~%" f (temperatura f))))
        
        ((= opcion 4)
         (format t "Adios amigo...~%")
         (return))  ;; Salir del loop
        
        (t
         (format t "Opción no válida~%"))))))


