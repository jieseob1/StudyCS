package theory;

interface AbstractProductA {
}

// productA-1
class ConcreteProductA1 implements AbstractProductA {
    public ConcreteProductA1() {
        System.out.println("theory.ConcreteProductA1 created.");
    }
}
//productA-2

class ConcreteProductA2 implements AbstractProductA {
    public ConcreteProductA2() {
        System.out.println("theory.ConcreteProductA2 created.");
    }
}
