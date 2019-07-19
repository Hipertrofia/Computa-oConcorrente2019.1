import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class SavingsAccount2 {
    int saldo;
    int preferredCount = 0;
    ReentrantLock lock = new ReentrantLock();
    Condition saldoCond = lock.newCondition();    

    SavingsAccount2(int saldo) {
        this.saldo = saldo;
    }

    void deposit(int valor) {
        try {
            lock.lock();
            this.saldo += valor;
            saldoCond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    void ordinarySaque(int valor) throws InterruptedException {
        try {
            lock.lock();
            while(this.saldo < valor || this.preferredCount > 0) {
                saldoCond.await();
            }
            this.saldo -= valor;
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void preferredSaque(int valor) {
        try {
            lock.lock();
            synchronized(this) {
                this.preferredCount++;
            }
            while(this.saldo < valor) {
                saldoCond.await();
            }
            this.saldo -= valor;
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            synchronized(this) {
                this.preferredCount--;
            }
            lock.unlock();
        }
    }    

    int getSaldo() {
        return this.saldo;
    }

    int getPreferredCount() {
        return this.preferredCount;
    }
}
