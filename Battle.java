package Module8.Praktika;

import Module8.Praktika.SeaBattle1.Ship;

import java.util.*;

public class Battle {

    private static Random random = new Random();

    private static List<Ship> ships = new ArrayList<>();
    private static List<Thread> threads = new ArrayList<>();
    static int countOfShips;

    private static Runnable startFight = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && ships.size() > 1) {
                if (!threads.contains(Thread.currentThread())) {
                    Thread.currentThread().interrupt();
                    break;
                } else {
                    synchronized (ships.get(threads.indexOf(Thread.currentThread()))) { // Корабль по идексу текущего потока
                        int thisShip = threads.indexOf(Thread.currentThread());
                        int otherShip = random.nextInt(threads.size());

                        if (thisShip == otherShip)
                            otherShip = changeIndexShip(thisShip, otherShip);

                        if (ships.get(otherShip).getHp() <= 0) {
                            killTheShip(otherShip);
                            if (ships.size() == 1) {
                                System.out.println(nameWinnerShip());
                                break;
                            } else {
                                thisShip = threads.indexOf(Thread.currentThread());
                                otherShip = changeIndexShip(thisShip, otherShip);
                            }
                        }
                        shoot(thisShip, otherShip);
                    }
                }
            }
        }
    };

    private static void createAndStartShips() {
        for (int i = 0; i < countOfShips; i++) {
            ships.add(new Ship());
            ships.get(i).setHp(random.nextInt(51) + 50);
            ships.get(i).setDamage(random.nextInt(11) + 20);
            ships.get(i).setCoolDown(0.2f + random.nextDouble() * 1.5f);
            threads.add(new Thread(startFight));
        }

        for (Thread thread : threads) {
            synchronized (threads) {
                if (threads.contains(thread) && !thread.isInterrupted())
                    thread.start();
            }
        }
    }

    private static void killTheShip(int otherShip) {
        System.out.println(threads.get(otherShip).getName() + " Died!");
        ships.remove(otherShip);
        threads.get(otherShip).interrupt();
        threads.remove(otherShip);
    }

    private static String nameWinnerShip() {
        Thread.currentThread().interrupt();
        return Thread.currentThread().getName() + " WIN!!!";
    }

    private static int changeIndexShip(int thisShip, int otherShip) {
        do {
            otherShip = random.nextInt(ships.size());
        } while (thisShip == otherShip);
        return otherShip;
    }

    private static void shoot(int thisShip, int otherShip) {
        ships.get(otherShip).setHp(ships.get(otherShip).getHp() - ships.get(thisShip).getDamage());
        System.out.println(Thread.currentThread().getName() + " shoot to " + threads.get(otherShip).getName());
        try {
            Thread.currentThread().sleep((long) (ships.get(thisShip).getCoolDown() * 1000));
            System.out.println(Thread.currentThread().getName() + " is recharged");
        } catch (InterruptedException e) {
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of ships - ");
        countOfShips = scanner.nextInt();
        createAndStartShips();
    }
}
