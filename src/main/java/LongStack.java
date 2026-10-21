// Kasutatud claude abi seletamiseks ja parandusteks

public class LongStack {

   private static class Node {
      long value;
      Node next;

      Node(long a) {
         value = a;
      }
   }

   private Node top;

   public static void main (String[] argum) {
      System.out.println(interpret("15 7 3 >R - R> -"));     // 5
      System.out.println(interpret("3 >R R@ R> *"));         // 9
      System.out.println(interpret("2 5 >R 3 R@ * - R> -")); // -18
   }

   LongStack() {
      top = null;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      LongStack copy = new LongStack();
      if (top == null) {
         return copy;
      }

      copy.top = new Node(top.value);
      Node original = top.next;
      Node last = copy.top;
      while (original != null) {
         Node n = new Node(original.value);
         last.next = n;
         last = n;
         original = original.next;
      }
      return copy;
   }

   public boolean stEmpty() {
      return top == null;
   }

   public void push (long a) {
      Node n = new Node(a);
      n.next = top;
      top = n;
   }

   public long pop() {
      if (stEmpty()) {
         throw new RuntimeException("pop: Can't remove element, stack is empty");
      }

      long value = top.value;
      top = top.next;
      return value;
   }

   public void op (String s) {
      if (top == null || top.next == null) {
         throw new RuntimeException("op: Stack must have at least two elements");
      }
      long b = pop();
      long a = pop();

      if (s.equals("+")) {
         push(a + b);
      }
      else if (s.equals("-")) {
         push(a - b);
      }
      else if (s.equals("*")) {
         push(a * b);
      }
      else if (s.equals("/")) {
         if (b == 0){
            throw new RuntimeException("Can't divide with 0");
         }
         push(a / b);
      }
      else {
         throw new RuntimeException("op: Unknown equation "+ s + ", allowed symbols +, -, *, /");
      }
   }

   public long tos() {
      if (stEmpty()) {
         throw new RuntimeException("tos: Can't read element, stack is empty");
      }

      return top.value;
   }

   @Override
   public boolean equals (Object o) {
      if (! (o instanceof LongStack)) {
         return false;
      }

      LongStack other = (LongStack) o;
      Node a = this.top;
      Node b = other.top;

      while (a != null && b != null) {
         if (a.value != b.value) {
            return false;
         }
         else {
            a = a.next;
            b= b.next;
         }
      }
      return a == null && b == null;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      Node current = top;
      while (current != null) {
         if (sb.length() > 0) {
            sb.insert(0, " ");
         }
         sb.insert(0, current.value);
         current = current.next;
      }
      return sb.toString();
   }

   public static long interpret (String pol) {
      if (pol == null || pol.trim().isEmpty()) { // Veatüüp a
         throw new RuntimeException("Avaldis puudub");
      }
      LongStack stack = new LongStack();   // andmepinu
      LongStack rstack = new LongStack();  // R-pinu (return stack)
      String[] tokens = pol.trim().split("\\s+");

      for (String token : tokens) {
         if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
            try {
               stack.op(token);
            } catch (RuntimeException e) { // Veatüüp d
               throw new RuntimeException("Viga avaldises \"" + pol + "\" tehtel \"" + token + "\": " + e.getMessage());
            }
         }
         else if (token.equals(">R")) {
            // andmepinus ( a -- ), R-pinus ( -- a )
            if (stack.stEmpty()) {
               throw new RuntimeException("Viga avaldises \"" + pol
                       + "\": >R ei saa täita, andmepinu on tühi");
            }
            rstack.push(stack.pop());
         }
         else if (token.equals("R>")) {
            // andmepinus ( -- a ), R-pinus ( a -- )
            if (rstack.stEmpty()) {
               throw new RuntimeException("Viga avaldises \"" + pol
                       + "\": R> ei saa täita, R-pinu on tühi");
            }
            stack.push(rstack.pop());
         }
         else if (token.equals("R@")) {
            // andmepinus ( -- a ), R-pinus ( a -- a )
            if (rstack.stEmpty()) {
               throw new RuntimeException("Viga avaldises \"" + pol
                       + "\": R@ ei saa täita, R-pinu on tühi");
            }
            stack.push(rstack.tos());
         }
         else {
            try {
               stack.push(Long.parseLong(token));
            } catch (NumberFormatException e) { // Veatüüp c
               throw new RuntimeException("Lubamatu sümbol \"" + token + "\" avaldises \"" + pol + "\"");
            }
         }
      }
      if (!rstack.stEmpty()) {
         throw new RuntimeException("Avaldises \"" + pol
                 + "\" jäi R-pinu lõpus tühjaks tegemata: " + rstack);
      }
      if (stack.stEmpty()) {
         throw new RuntimeException("Avaldises \"" + pol + "\" ei jäänud andmepinusse tulemust");
      }
      long result = stack.pop();
      if (!stack.stEmpty()) { // Veatüüp b
         throw new RuntimeException("Avaldises \"" + pol + "\" liiga palju arve");
      }
      return result;
   }
}