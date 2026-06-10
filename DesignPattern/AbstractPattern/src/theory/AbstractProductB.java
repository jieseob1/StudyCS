package theory;

interface AbstractProductB {
}

// productB-1
class ConcreteProductB1 implements AbstractProductB {
    public ConcreteProductB1() {
        System.out.println("theory.ConcreteProductB1 created.");
    }
}

// productB-2
class ConcreteProductB2 implements AbstractProductB {
    public ConcreteProductB2() {
        System.out.println("theory.ConcreteProductB2 created.");
    }
}
