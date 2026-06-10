package theory;

public class Client {
    public static void main(String[] args) {
        AbstractFactory factory = null;

        // 공장1 가동
        factory = new ConcreteFactory1();

        AbstractProductA productA1 = factory.createProductA(); // theory.ConcreteProductA1 객체 생성
        System.out.println("Factory 1 created: " + productA1.getClass().getSimpleName());
        factory = new ConcreteFactory2();

        AbstractProductA productA2 = factory.createProductA(); // theory.ConcreteProductA2 객체 생성
        System.out.println("Factory 2 created: " + productA2.getClass().getSimpleName());

        // 똑같은 createProductB() 메서드를 호출하지만, 팩토리에 따라 다른 제품이 생성됨



    }
}
