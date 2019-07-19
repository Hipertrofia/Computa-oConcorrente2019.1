import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class SavingsAccount1 {
    int saldo;
    ReentrantLock lock = new ReentrantLock();
	Condition saldoCond = lock.newCondition();

    SavingsAccount1(int saldo) {
        this.saldo = saldo;
    }

    int getSaldo() {
        return this.saldo;
    }

    void deposito(int valor) {
        try {
            lock.lock();
            this.saldo += valor;
            saldoCond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    void saque(int valor) throws InterruptedException {
        try {
            lock.lock();
            while(this.saldo < valor) {
                saldoCond.await();
            }
            this.saldo -= valor;
        } finally {
            lock.unlock();
        }
    }
}
